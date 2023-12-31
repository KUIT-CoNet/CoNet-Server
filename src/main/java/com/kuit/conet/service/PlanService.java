package com.kuit.conet.service;

import com.kuit.conet.common.exception.PlanException;
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
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

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
        LocalDate date = createPlanRequest.getPlanStartPeriod().toLocalDate().plusDays(6);
        Date endDate = Date.valueOf(date);
        Plan plan = new Plan(createPlanRequest.getTeamId(), createPlanRequest.getPlanName(), createPlanRequest.getPlanStartPeriod(), endDate);

        Long planId = planDao.savePlan(plan);

        return new CreatePlanResponse(planId);
    }

    public void saveTime(PossibleTimeRequest possibleTimeRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        // 대기 중인 약속일 때만 시간 저장
        if (!planDao.isWaitingPlan(possibleTimeRequest.getPlanId())) {
            throw new PlanException(NOT_WAITING_PLAN);
        }

        for (PossibleDateTime possibleDateTime : possibleTimeRequest.getPossibleDateTimes()) {
            // 7개의 날에 대하여 가능한 시간 저장
            Date possibleDate = possibleDateTime.getDate();

            // 7개의 날에 대하여 가능한 시간을 문자열로 변환
            String possibleTimes = setPossibleTimeToString(possibleDateTime);

            PlanMemberTime planMemberTime = new PlanMemberTime(possibleTimeRequest.getPlanId(), userId, possibleDate, possibleTimes);
            planDao.deletePossibleDate(planMemberTime); // 기존 데이터 삭제
            planDao.saveTime(planMemberTime);
        }
    }

    // 특정 날짜에 가능한 시간을 문자열로 변환
    private String setPossibleTimeToString(PossibleDateTime possibleDateTime) {
        StringBuilder sb = new StringBuilder();
        List<Integer> times = possibleDateTime.getTime();
        if (times.isEmpty()) return "";

        for (Integer time : times) {
            sb.append(time).append(", ");
        }

        String strTime = sb.toString().trim().substring(0, sb.length()-2);
        return strTime;
    }

    public UserTimeResponse getUserTime(PlanIdRequest planIdRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        // 대기 중인 약속일 때만 나의 가능한 시간 조회
        if (!planDao.isWaitingPlan(planIdRequest.getPlanId())) {
            throw new PlanException(NOT_WAITING_PLAN);
        }

        if(!planDao.isExistingUserTime(planIdRequest.getPlanId(), userId)) {
            return new UserTimeResponse(planIdRequest.getPlanId(), userId, false, false, null);
        }

        return planDao.getUserTime(planIdRequest.getPlanId(), userId);
    }

    public MemberPossibleTimeResponse getMemberTime(PlanIdRequest planIdRequest) {
        // 대기 중인 약속일 때만 구성원의 가능한 시간 조회
        if (!planDao.isWaitingPlan(planIdRequest.getPlanId())) {
            throw new PlanException(NOT_WAITING_PLAN);
        }

        Plan plan = planDao.getWaitingPlan(planIdRequest.getPlanId());

        if(plan == null) {
            throw new PlanException(NOT_WAITING_PLAN);
        }

        Date date = plan.getPlanStartPeriod();
        Long teamId = planDao.getTeamId(planIdRequest.getPlanId());

        Long count = teamDao.getTeamMemberCount(teamId);

        List<MemberDateTimeResponse> memberDateTimeResponses = new ArrayList<>(7);

        // 날짜 하루씩 더해가면서 7일에 대한 구성원의 모든 가능한 시간 저장하는 것 반복
        for(int j=0; j<7; j++) {
            int[] membersCount = new int[24];

            List<MemberResponse> memberResponses = new ArrayList<>(24);
            ArrayList<ArrayList<String>> memberNames  = new ArrayList<>(24);
            ArrayList<ArrayList<Long>> memberIds  = new ArrayList<>(24);
            for(int i=0; i<24; i++) {
                memberNames.add(new ArrayList<>());
                memberIds.add(new ArrayList<>());
            }

            List<MemberPossibleTime> memberPossibleTimes = planDao.getMemberTime(planIdRequest.getPlanId(), date);
            // [{userId, possibleTime}, {1, "1, 2, 3, 4"}, {2, "3, 4, 5, 6"}, ... , {4, "5, 6, 7, 8"}]

            for(MemberPossibleTime memberPossibleTime : memberPossibleTimes) {
                String possibleTime = memberPossibleTime.getPossibleTime();  // "1, 2, 3, 4"

                if (possibleTime.isEmpty()) {
                    continue;
                }

                possibleTime = possibleTime.replaceAll(" ", "");  // "1,2,3,4"
                String[] possibleTimes = possibleTime.split(",");  // ["1", "2", "3", "4"]

                for(String time : possibleTimes) {
                    int intTime = Integer.parseInt(time);
                    if(0 <= intTime && intTime <= 23) {
                        membersCount[intTime]++;
                        // [1, 0, 0, ... 0, 0]  각 시간에 가능한 구성원의 수

                        memberNames.get(intTime).add(userDao.getUserName(memberPossibleTime.getUserId()));
                        memberIds.get(intTime).add(memberPossibleTime.getUserId());
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

            for(int i=0; i<24; i++) {
                MemberResponse memberResponse = new MemberResponse(i, membersCount[i], memberNames.get(i), memberIds.get(i));
                memberResponses.add(memberResponse);
            }

            memberDateTimeResponses.add(new MemberDateTimeResponse(date, memberResponses));
            /** 하루에 대한 가능한 구성원 정보 추가 완료*/

            // 약속 기간 시작 날짜에서 하루씩 더하기
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);  // Calender 타입으로 변환해서 하루 더하기
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(cal.getTime());
            date = java.sql.Date.valueOf(formattedDate);  // Calender -> java.sql.Date 타입 변경
        }

        List<SectionMemberCount> sectionMemberCounts = new ArrayList<>(3);
        if (3 < count) {
            Long countLong = count/3;
            Integer countInt = countLong.intValue();

            SectionMemberCount sectionMemberCount = new SectionMemberCount();
            sectionMemberCount.setSection(1);
            List<Integer> memberCount = new ArrayList<>();
            for (int i=1; i<countInt+1; i++) {
                memberCount.add(i);
            }
            sectionMemberCount.setMemberCount(memberCount);
            sectionMemberCounts.add(sectionMemberCount);


            sectionMemberCount = new SectionMemberCount();
            sectionMemberCount.setSection(2);
            memberCount = new ArrayList<>();
            for (int i=countInt+1; i<(countInt*2)+1; i++) {
                memberCount.add(i);
            }
            sectionMemberCount.setMemberCount(memberCount);
            sectionMemberCounts.add(sectionMemberCount);


            sectionMemberCount = new SectionMemberCount();
            sectionMemberCount.setSection(3);
            memberCount = new ArrayList<>();
            for (int i=(countInt*2)+1; i<=count; i++) {
                memberCount.add(i);
            }
            sectionMemberCount.setMemberCount(memberCount);
            sectionMemberCounts.add(sectionMemberCount);
        } else {
            for (int i=1; i<=count; i++) {
                SectionMemberCount sectionMemberCount = new SectionMemberCount();
                sectionMemberCount.setSection(i);
                List<Integer> memberCount = new ArrayList<>();
                memberCount.add(i);
                sectionMemberCount.setMemberCount(memberCount);
                sectionMemberCounts.add(sectionMemberCount);
            }
        }

        MemberPossibleTimeResponse memberPossibleTimeResponse =
                new MemberPossibleTimeResponse(teamId, planIdRequest.getPlanId(), plan.getPlanName(),
                        plan.getPlanStartPeriod(), plan.getPlanEndPeriod(), sectionMemberCounts, memberDateTimeResponses);

        return memberPossibleTimeResponse;
    }

    public String fixPlan(FixPlanRequest fixPlanRequest) {
        Long longTime = fixPlanRequest.getFixed_time();

        String strTime = longTime.toString() + ":00:00";

        Time time = Time.valueOf(strTime);

        if(planDao.isFixedPlan(fixPlanRequest.getPlanId())){
            throw new PlanException(ALREADY_FIXED_PLAN);
        }
        planDao.fixPlan(fixPlanRequest.getPlanId(), fixPlanRequest.getFixed_date(), time, fixPlanRequest.getUserId());
        planDao.deletePlanMemberTime(fixPlanRequest.getPlanId());

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

    public TeamPlanOnDayResponse getPlanOnDay(TeamFixedPlanRequest planRequest) {
        List<TeamFixedPlanOnDay> plans = planDao.getPlanOnDay(planRequest.getTeamId(), planRequest.getSearchDate()); // yyyy-MM-dd

        return new TeamPlanOnDayResponse(plans.size(), plans);
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

        return details;
    }

    public String deletePlan(PlanIdRequest planRequest) {
        Long planId = planRequest.getPlanId();
        Boolean isFixedPlan;

        if (planDao.isFixedPlan(planId)) isFixedPlan = true;
        else isFixedPlan = false;

        if (planDao.isRegisteredToHistory(planId)) { // 히스토리 등록된 약속
            // 이미지 객체 삭제
            String imgUrl = historyDao.getHistoryImgUrl(planId);
            String deleteFileName = storageService.getFileNameFromUrl(imgUrl);
            storageService.deleteImage(deleteFileName);

            historyDao.deleteHistory(planId);
        }

        planDao.deletePlan(planId, isFixedPlan);
        return "약속 삭제에 성공하였습니다.";
    }

    public String updateWaitingPlan(UpdateWaitingPlanRequest planRequest) {
        if (!planDao.isWaitingPlan(planRequest.getPlanId())) {
            throw new PlanException(NOT_WAITING_PLAN);
        }

        planDao.updateWaitingPlan(planRequest.getPlanId(), planRequest.getPlanName());
        return "약속 수정을 성공하였습니다.";
    }

    public String updateFixedPlan(UpdatePlanRequest planRequest, MultipartFile file) {
        Long planId = planRequest.getPlanId();

        if (!planDao.isFixedPlan(planId)) {
            throw new PlanException(NOT_FIXED_PLAN);
        }

        // plan 테이블 정보 수정
        planDao.updateFixedPlan(planRequest);

        // 이미 history 에 등록된 약속이면 history 수정
        if (planRequest.getIsRegisteredToHistory()) {
            // 기존 이미지 삭제 작업 진행 - 존재하지 않으면 생략
            if (historyDao.isHistoryImageExist(planId)) {
                String imgUrl = historyDao.getHistoryImgUrl(planId);
                String deleteFileName = storageService.getFileNameFromUrl(imgUrl);
                storageService.deleteImage(deleteFileName);
            }

            // 수정된 값의 이미지와 상세 내용 존재 여부를 판단
            // -> 둘 다 null 인 경우 히스토리 데이터 삭제 및 plan 테이블에 history=0 으로 수정
            if (planRequest.getHistoryDescription()==null && file.isEmpty()) {
                log.info("히스토리 정보가 비어있습니다. 해당 약속의 히스토리 데이터를 삭제합니다.");

                // 기존 히스토리 정보에 이미지 존재시 S3 객체 삭제
                planDao.setHistoryInactive(planId);
                historyDao.deleteHistory(planId);
            } else {
                String imgUrl = null;
                if (!file.isEmpty()) {
                    // 저장할 파일명 만들기 - 받은 파일이 이미지 타입이 아닌 경우에 대한 유효성 검사 진행
                    String fileName = storageService.getFileName(file, StorageDomain.HISTORY, planId);
                    // 새로운 이미지 S3에 업로드
                    imgUrl = storageService.uploadToS3(file, fileName);
                }

                History newHistory = new History(imgUrl, planRequest.getHistoryDescription());

                historyDao.updateHistory(newHistory, planId);
            }
        }

        return "약속 정보를 수정하였습니다.";
    }

    public List<PastPlan> getPastPlan(TeamIdRequest planRequest) {
        Long teamId = planRequest.getTeamId();
        return planDao.getPastPlan(teamId);
    }

    // 팀 사이드바 - 지나지 않은 확정 약속 조회
    public List<SideMenuFixedPlan> getFixedPlan(TeamIdRequest planRequest) {
        Long teamId = planRequest.getTeamId();
        return planDao.getFixedPlan(teamId);
    }

    public List<PastPlan> getNotRegisteredToHistoryPlan(TeamIdRequest planRequest) {
        Long teamId = planRequest.getTeamId();
        return planDao.getNotRegisteredToHistoryPlan(teamId);
    }

    public List<MemberIsInPlanResponse> getMemberIsInPlan(PlanIdRequest planIdRequest) {
        Long planId = planIdRequest.getPlanId();

        return planDao.getMemberIsInPlanId(planId);
    }
}