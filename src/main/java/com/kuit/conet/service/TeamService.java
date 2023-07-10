package com.kuit.conet.service;

import com.kuit.conet.dao.TeamDao;
import com.kuit.conet.domain.Team;
import com.kuit.conet.dto.request.team.MakeTeamRequest;
import com.kuit.conet.dto.response.team.MakeTeamResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamDao teamDao;
    public MakeTeamResponse makeTeam(MakeTeamRequest request) {
        // 초대 코드 생성
        String inviteCode;

        // 코드 중복 확인
        do {
            inviteCode = generateInviteCode();
        } while(teamDao.validateDuplicateCode(inviteCode));  // 중복되면 true 반환

        // 모임 생성 시간 찍기
        Timestamp codeGeneratedTime = Timestamp.valueOf(LocalDateTime.now());

        // team table에 새로운 team insert하고 teamId 얻기
        Team newTeam = new Team(request.getTeamName(), request.getTeamImgUrl(), inviteCode, codeGeneratedTime);
        Long teamId = teamDao.saveTeam(newTeam);

        // teamMembers 에 userId 추가
        Long teamMemberId = teamDao.saveTeamMember(teamId, request.getUserId());

        return new MakeTeamResponse(teamId, inviteCode, codeGeneratedTime);
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

}
