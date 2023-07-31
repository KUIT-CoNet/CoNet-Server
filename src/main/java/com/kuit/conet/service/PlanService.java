package com.kuit.conet.service;

import com.kuit.conet.common.exception.TeamException;
import com.kuit.conet.dao.HistoryDao;
import com.kuit.conet.dao.PlanDao;
import com.kuit.conet.dao.TeamDao;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.domain.history.History;
import com.kuit.conet.domain.plan.*;
import com.kuit.conet.domain.storage.StorageDomain;
import com.kuit.conet.dto.request.plan.*;
import com.kuit.conet.dto.request.team.TeamIdRequest;
import com.kuit.conet.dto.response.plan.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_TEAM;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanDao planDao;
    private final UserDao userDao;
    private final TeamDao teamDao;
    private final HistoryDao historyDao;
    private final StorageService storageService;

    public CreatePlanResponse createPlan(CreatePlanRequest createPlanRequest) {
        LocalDate endDate = createPlanRequest.getPlanStartPeriod().toLocalDate();
        endDate = endDate.plusDays(6);
        Date date = java.sql.Date.valueOf(endDate);
        Plan plan = new Plan(createPlanRequest.getTeamId(), createPlanRequest.getPlanName(), createPlanRequest.getPlanStartPeriod(), date);

        Long planId = planDao.savePlan(plan);

        return new CreatePlanResponse(planId);
    }

    public void saveTime(PossibleTimeRequest possibleTimeRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        String strTime = "";
        for(Integer time : possibleTimeRequest.getPossibleTime()) {
            strTime += time.toString() + ", ";
        }

        strTime = strTime.trim().substring(0, strTime.length()-2);

        PlanMemberTime planMemberTime = new PlanMemberTime(possibleTimeRequest.getPlanId(), userId, possibleTimeRequest.getPossibleDate(), strTime);

        // 대기 중인 약속일 때만 시간 저장
        if(planDao.isWaitingPlan(possibleTimeRequest.getPlanId())) {
            // 이미 저장되어 있는 날짜이면 삭제 후 저장
            if(planDao.isExistingUserDate(planMemberTime)) {
                planDao.deletePossibleDate(planMemberTime);
            }

            planDao.saveTime(planMemberTime);
        }
    }

    public UserTimeResponse getUserTime(PlanIdRequest planIdRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        if(!planDao.isExistingUserTime(planIdRequest.getPlanId(), userId)) {
            return new UserTimeResponse(planIdRequest.getPlanId(), userId, null);
        }

        return planDao.getUserTime(planIdRequest.getPlanId(), userId);
    }

    public MemberPossibleTimeResponse getMemberTime(PlanIdRequest planIdRequest) {
        Plan plan = planDao.getWaitingPlan(planIdRequest.getPlanId());
        if(plan == null) {
            log.warn("대기 중인 약속이 아닙니다.");
        }
        Date date = plan.getPlanStartPeriod();
        Long teamId = planDao.getTeamId(planIdRequest.getPlanId());
        if(!teamDao.isExistTeam(teamId)) {
            throw new TeamException(NOT_FOUND_TEAM);
        }
        Long count = teamDao.getTeamMemberCount(teamId);

        List<MemberResponse> memberResponses = new ArrayList<>(24);
        List<MemberDateTimeResponse> memberDateTimeResponses = new ArrayList<>(7);

        // 날짜 하루씩 더해가면서 7일에 대한 구성원의 모든 가능한 시간 저장하는 것 반복
        for(int j=0; j<7; j++) {
            int[] membersCount = new int[24];

            ArrayList<ArrayList<String>> membersName  = new ArrayList<>(24);
            for(int i=0; i<24; i++) {
                membersName.add(new ArrayList<String>());
            }

            List<MemberPossibleTime> memberPossibleTimes = planDao.getMemberTime(planIdRequest.getPlanId(), date);
            // [{userId, possibleTime}, {1, "1, 2, 3, 4"}, {2, "3, 4, 5, 6"}, ... , {4, "5, 6, 7, 8"}]

            for(MemberPossibleTime memberPossibleTime : memberPossibleTimes) {
                String possibleTime = memberPossibleTime.getPossibleTime();  // "1, 2, 3, 4"
                possibleTime = possibleTime.replaceAll(" ", "");  // "1,2,3,4"
                String[] possibleTimes = possibleTime.split(",");  // ["1", "2", "3", "4"]

                for(String time : possibleTimes) {
                    int intTime = Integer.parseInt(time);
                    if(0 <= intTime && intTime <= 23) {
                        membersCount[intTime]++;
                        // [1, 0, 0, ... 0, 0]  각 시간에 가능한 구성원의 수

                        membersName.get(intTime).add(userDao.getUserName(memberPossibleTime.getUserId()));
                        // [("정소민, "), (""), (""), ... , (""), ("")]  각 시간에 가능한 구성원
                        // [("정소민, 정경은, "), ("정소민, "), ("정소민, 이안진, "), ... , (""), ("정경은, 이안진, ")]  이렇게 채워질 것
                    }
                }
            }

            if(3 < count) {
                for(int i=0; i<membersCount.length; i++) {
                    if(membersCount[i] == 0) continue;

                    if(0 < membersCount[i] && membersCount[i] <= count/3) {
                        membersCount[i] = 1;
                    }

                    if(count/3 < membersCount[i] && membersCount[i] <= (count/3)*2) {
                        membersCount[i] = 2;
                    }

                    if((count/3)*2 < membersCount[i]) {
                        membersCount[i] = 3;
                    }
                }
            }

            for(int i=0; i<membersCount.length; i++) {
                if(membersCount[i] == 0) {
                    continue;
                }

                MemberResponse memberResponse = new MemberResponse(i, membersCount[i], membersName.get(i));
                memberResponses.add(memberResponse);
            }

            List<List<MemberResponse>> tempListMemberResponses = new ArrayList<>(7);

            for(int i=0; i<7; i++) {
                List<MemberResponse> tempMemberResponses = new ArrayList<>();
                tempListMemberResponses.add(tempMemberResponses);
            }

            tempListMemberResponses.get(j).addAll(memberResponses);
            memberResponses.clear();

            MemberDateTimeResponse memberDateTimeResponse = new MemberDateTimeResponse(date, tempListMemberResponses.get(j));
            memberDateTimeResponses.add(memberDateTimeResponse);

            // 약속 기간 시작 날짜에서 하루씩 더하기
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);  // Calender 타입으로 변환해서 하루 더하기
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(cal.getTime());
            date = java.sql.Date.valueOf(formattedDate);  // Calender -> java.sql.Date 타입 변경
        }

        MemberPossibleTimeResponse memberPossibleTimeResponse =
                new MemberPossibleTimeResponse(teamId, planIdRequest.getPlanId(), plan.getPlanName(),
                        plan.getPlanStartPeriod(), plan.getPlanEndPeriod(), memberDateTimeResponses);

        return memberPossibleTimeResponse;
    }

    public String fixPlan(FixPlanRequest fixPlanRequest) {
        Long longTime = fixPlanRequest.getFixed_time();

        String strTime = longTime.toString() + ":00:00";

        Time time = Time.valueOf(strTime);

        if(planDao.isFixedPlan(fixPlanRequest.getPlanId())){
            return "이미 확정된 약속입니다.";
        }
        planDao.fixPlan(fixPlanRequest.getPlanId(), fixPlanRequest.getFixed_date(), time, fixPlanRequest.getUserId());
        return "약속 확정에 성공하였습니다.";
    }

    public MonthPlanResponse getPlanInMonth(TeamFixedPlanRequest planRequest) {
        List<Integer> planDates = new ArrayList<>();

        List<String> dateList = planDao.getPlanInMonth(planRequest.getTeamId(), planRequest.getSearchDate()); // yyyy-MM
        for(String tempDate : dateList) {
            Integer date = Integer.parseInt(tempDate.split("-")[2]);
            planDates.add(date);
        }

        return new MonthPlanResponse(planDates.size(), planDates);
    }

    public DayPlanResponse getPlanOnDay(TeamFixedPlanRequest planRequest) {
        List<FixedPlan> plans = planDao.getPlanOnDay(planRequest.getTeamId(), planRequest.getSearchDate()); // yyyy-MM-dd

        return new DayPlanResponse(plans.size(), plans);
    }

    public WaitingPlanResponse getWaitingPlan(TeamWaitingPlanRequest planRequest) {
        List<WaitingPlan> plans = planDao.getWaitingPlanInTeam(planRequest.getTeamId());

        return new WaitingPlanResponse(plans.size(), plans);
    }

    public PlanDetail getPlanDetail(PlanIdRequest planRequest) {
        Long planId = planRequest.getPlanId();

        // 히스토리 등록 여부
        Boolean isRegisteredToHistory = planDao.isRegisteredToHistory(planId);
        log.info("isRegisteredToHistory: {}", isRegisteredToHistory);

        // 약속 상세 정보
        PlanDetail details = planDao.getPlanDetail(planRequest.getPlanId(), isRegisteredToHistory);

        // TODO: history 에서 검색은 되지만, 이미지랑 설명이 모두 null 인 경우 history 테이블에서 삭제 후 isRegisteredToHistory false 처리

        return details;
    }

    public String deletePlan(PlanIdRequest planRequest) {
        planDao.deletePlan(planRequest.getPlanId());
        return "약속 삭제에 성공하였습니다.";
    }

    public String updateWaitingPlan(UpdateWaitingPlanRequest planRequest) {
        if (!planDao.isWaitingPlan(planRequest.getPlanId())) {
            return "대기 중인 약속이 아닙니다.";
        }

        planDao.updateWaitingPlan(planRequest.getPlanId(), planRequest.getPlanName());
        return "약속 수정을 성공하였습니다.";
    }

    public String updateFixedPlan(UpdatePlanRequest planRequest, MultipartFile file) {
        Long planId = planRequest.getPlanId();

        if (!planDao.isFixedPlan(planId)) {
            return "확정된 약속이 아닙니다.";
        }

        // plan 테이블 정보 수정
        planDao.updateFixedPlan(planRequest);

        // history 있으면 history 수정
        if (planRequest.getIsRegisteredToHistory()) {
            // 기존 이미지 삭제 작업 진행 - 존재하지 않으면 생략
            if (historyDao.isHistoryImageExist(planId)) {
                String imgUrl = historyDao.getHistoryImgUrl(planId);
                String deleteFileName = storageService.getFileNameFromUrl(imgUrl);
                storageService.deleteImage(deleteFileName);
            }

            // 저장할 파일명 만들기 - 받은 파일이 이미지 타입이 아닌 경우에 대한 유효성 검사 진행
            String fileName = storageService.getFileName(file, StorageDomain.HISTORY, planId);
            // 새로운 이미지 S3에 업로드
            String imgUrl = storageService.uploadToS3(file, fileName);

            History newHistory = new History(imgUrl, planRequest.getHistoryDescription());

            historyDao.updateHistory(newHistory, planId);
        }

        return "약속 정보를 수정하였습니다.";
    }

    public List<PastPlan> getPastPlan(TeamIdRequest planRequest) {
        Long teamId = planRequest.getTeamId();
        return planDao.getPastPlan(teamId);
    }

    public List<FixedPlan> getFixedPlan(TeamIdRequest planRequest) {
        Long teamId = planRequest.getTeamId();
        return planDao.getFixedPlan(teamId);
    }

    public List<PastPlan> getNotRegisteredToHistoryPlan(TeamIdRequest planRequest) {
        Long teamId = planRequest.getTeamId();
        return planDao.getNotRegisteredToHistoryPlan(teamId);
    }
}