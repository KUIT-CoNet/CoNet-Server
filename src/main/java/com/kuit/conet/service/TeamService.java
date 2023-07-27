package com.kuit.conet.service;

import com.kuit.conet.common.exception.TeamException;
import com.kuit.conet.dao.TeamDao;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.domain.storage.StorageDomain;
import com.kuit.conet.domain.team.Team;
import com.kuit.conet.domain.team.TeamMember;
import com.kuit.conet.dto.request.team.CreateTeamRequest;
import com.kuit.conet.dto.request.team.ParticipateTeamRequest;
import com.kuit.conet.dto.request.team.TeamIdRequest;
import com.kuit.conet.dto.request.team.UpdateTeamRequest;
import com.kuit.conet.dto.response.StorageImgResponse;
import com.kuit.conet.dto.response.team.CreateTeamResponse;
import com.kuit.conet.dto.response.team.GetTeamResponse;
import com.kuit.conet.dto.response.team.ParticipateTeamResponse;
import com.kuit.conet.utils.JwtParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final StorageService storageService;
    private final TeamDao teamDao;
    private final UserDao userDao;
    private final JwtParser jwtParser;

    private final String URL_SPLITER = "/";

    public CreateTeamResponse createTeam(CreateTeamRequest createTeamRequest, HttpServletRequest httpRequest, MultipartFile file) {
        // 초대 코드 생성
        String inviteCode;

        // 코드 중복 확인
        do {
            inviteCode = generateInviteCode();
        } while(teamDao.validateDuplicateCode(inviteCode));  // 중복되면 true 반환

        // 모임 생성 시간 찍기
        Timestamp codeGeneratedTime = Timestamp.valueOf(LocalDateTime.now());

        // team table에 새로운 team insert하고 teamId 얻기
        Team newTeam = new Team(createTeamRequest.getTeamName(), null, inviteCode, codeGeneratedTime);
        Long teamId = teamDao.saveTeam(newTeam);

        // 새로운 이미지 S3에 업로드
        String fileName = storageService.getFileName(file, StorageDomain.TEAM, teamId);
        String imgUrl = storageService.uploadToS3(file, fileName);

        StorageImgResponse response = teamDao.updateImg(teamId, imgUrl);

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        // teamMember 에 user 추가
        TeamMember newTeamMember = new TeamMember(teamId, userId);
        TeamMember savedTeamMember = teamDao.saveTeamMember(newTeamMember);

        return new CreateTeamResponse(savedTeamMember.getTeamId(), inviteCode);
    }

    public CreateTeamResponse regenerateCode(TeamIdRequest request) {
        // 초대 코드 생성
        String inviteCode;

        // 코드 중복 확인
        do {
            inviteCode = generateInviteCode();
        } while(teamDao.validateDuplicateCode(inviteCode));  // 중복되면 true 반환

        // 모임 생성 시간 찍기
        Timestamp codeGeneratedTime = Timestamp.valueOf(LocalDateTime.now());

        // 모임 존재 여부 확인
        if (!teamDao.isExistTeam(request.getTeamId())) {
            throw new TeamException(NOT_FOUND_TEAM);
        }

        // 초대 코드, 생성시간 update
        String newCode = teamDao.codeUpdate(request.getTeamId(), inviteCode, codeGeneratedTime);

        return new CreateTeamResponse(request.getTeamId(), newCode);
    }

    public String generateInviteCode() {
        int leftLimit = 48;
        int rigntLimit = 122;
        int targetStirngLength = 8;

        Random random = new Random();

        String generatedString = random.ints(leftLimit, rigntLimit+1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStirngLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public ParticipateTeamResponse participateTeam(ParticipateTeamRequest participateRequest, HttpServletRequest httpRequest) {
        // 모임 참가 요청 시간 찍기
        LocalDateTime participateRequestTime = LocalDateTime.now();

        // 초대 코드 존재 확인
        String inviteCode = participateRequest.getInviteCode();
        if (!teamDao.validateDuplicateCode(inviteCode)) {
            throw new TeamException(NOT_FOUND_INVITE_CODE);
        }

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        String userName = userDao.findById(userId).getName();

        Team team = teamDao.getTeamFromInviteCode(inviteCode);

        // 모임에 이미 존재하는 회원인지 확인
        if (teamDao.isExistingUser(team.getTeamId(), userId)) {
            throw new TeamException(EXIST_USER_IN_TEAM);
        }

        // 초대 코드 생성 시간과 모임 참가 요청 시간 비교
        LocalDateTime generatedTime = team.getCodeGeneratedTime().toLocalDateTime();
        LocalDateTime expirationDateTime = generatedTime.plusDays(1);

        log.info("generatedTime: {}", generatedTime);
        log.info("expirationDateTime: {}", expirationDateTime);
        log.info("participateRequestTime: {}", participateRequestTime);

        if (participateRequestTime.isAfter(expirationDateTime)) {
            // 초대 코드 생성 시간으로부터 1일이 지났으면 exception
            log.info("유효 기간 만료: {}", EXPIRED_INVITE_CODE.getMessage());
            throw new TeamException(EXPIRED_INVITE_CODE);
        }

        // teamMember 에 userId 추가
        TeamMember newTeamMember = new TeamMember(team.getTeamId(), userId);
        TeamMember savedTeamMember = teamDao.saveTeamMember(newTeamMember);

        return new ParticipateTeamResponse(userName, team.getTeamName(), savedTeamMember.getStatus());
    }

    public List<GetTeamResponse> getTeam(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        List<Team> teamResponses = teamDao.getTeam(userId);
        List<GetTeamResponse> teamReturnResponses = new ArrayList<>();

        // 모임의 created_at 시간 비교해서 3일 안지났으면 new_update 필드 1, 지났으면 0으로 update
        for(Team list : teamResponses) {
            Timestamp createdTime = teamDao.getCreatedTime(list.getTeamId());
            // Timestamp를 Instant로 변환
            Instant instant = createdTime.toInstant();
            // Instant를 LocalDateTime으로 변환 (기본 시스템의 ZoneId 사용)
            LocalDateTime time = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

            LocalDateTime now = LocalDateTime.now();

            if(now.minusDays(3).isAfter(time)) {
                teamDao.updatdIsNew(0, list.getTeamId());
            }
        }

        teamResponses = teamDao.getTeam(userId);

        // response 생성
        for(Team list : teamResponses)
            teamReturnResponses.add(new GetTeamResponse(list.getTeamId(), list.getTeamName(), list.getTeamImgUrl(), list.getIsNew()));

        // 모임의 구성원 수 받고 response에 넣음
        for(int i=0; i<teamResponses.size(); i++) {
            teamReturnResponses.get(i).setBookmark(teamDao.getBookmark(userId, teamResponses.get(i).getTeamId()));
            teamReturnResponses.get(i).setTeamMemberCount(teamDao.getTeamMemberCount(teamResponses.get(i).getTeamId()));
        }

        return teamReturnResponses;
    }

    public String leaveTeam(TeamIdRequest teamIdRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        // 모임 존재 여부 확인
        if (!teamDao.isExistTeam(teamIdRequest.getTeamId())) {
            throw new TeamException(NOT_FOUND_TEAM);
        }

        if (teamDao.leaveTeam(teamIdRequest.getTeamId(), userId)) {
             return "모임 탈퇴에 실패하였습니다.";
        }

        return "모임 탈퇴에 성공하였습니다.";
    }

    public String deleteTeam(TeamIdRequest teamIdRequest) {
        // 모임 존재 여부 확인
        if (!teamDao.isExistTeam(teamIdRequest.getTeamId())) {
            throw new TeamException(NOT_FOUND_TEAM);
        }

        if (teamDao.deleteTeam(teamIdRequest.getTeamId())) {
            return "모임 삭제에 실패하였습니다.";
        }

        return "모임 삭제에 성공하였습니다.";
    }

    public StorageImgResponse updateTeam(UpdateTeamRequest updateTeamRequest, MultipartFile file) {
        String fileName = storageService.getFileName(file, StorageDomain.TEAM, updateTeamRequest.getTeamId());

        if(!teamDao.isExistTeam(updateTeamRequest.getTeamId())) {
            return null;
        }

        String imgUrl = null;

        imgUrl = teamDao.getTeamImgUrl(updateTeamRequest.getTeamId());
        if(imgUrl != null) {
            String deleteFileName = storageService.getFileNameFromUrl(imgUrl);
            storageService.deleteImage(deleteFileName);
        }

        // 새로운 이미지 S3에 업로드
        imgUrl = storageService.uploadToS3(file, fileName);

        // image update
        StorageImgResponse response = teamDao.updateImg(updateTeamRequest.getTeamId(), imgUrl);

        // name update
        teamDao.updateName(updateTeamRequest.getTeamId(), updateTeamRequest.getTeamName());

        return response;
    }

    public List<String> getTeamMembers(TeamIdRequest teamIdRequest) {
        return teamDao.getTeamMembers(teamIdRequest.getTeamId());
    }
}
