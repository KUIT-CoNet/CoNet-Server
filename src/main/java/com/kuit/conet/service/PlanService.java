package com.kuit.conet.service;

import com.kuit.conet.common.exception.TeamException;
import com.kuit.conet.dao.PlanDao;
import com.kuit.conet.dao.TeamDao;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.domain.MemberPossibleTime;
import com.kuit.conet.domain.Plan;
import com.kuit.conet.domain.PlanMemberTime;
import com.kuit.conet.dto.request.plan.CreatePlanRequest;
import com.kuit.conet.dto.request.plan.PossibleTimeRequest;
import com.kuit.conet.dto.request.plan.PlanIdRequest;
import com.kuit.conet.dto.response.plan.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_TEAM;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanDao planDao;
    private final UserDao userDao;
    private final TeamDao teamDao;

    public CreatePlanResponse createPlan(CreatePlanRequest createPlanRequest) {
        Plan plan = new Plan(createPlanRequest.getTeamId(), createPlanRequest.getPlanName(), createPlanRequest.getPlanStartPeriod(), createPlanRequest.getPlanEndPeriod());

        Long planId = planDao.savePlan(plan);

        return new CreatePlanResponse(planId);
    }

    public void saveTime(PossibleTimeRequest possibleTimeRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        PlanMemberTime planMemberTime = new PlanMemberTime(possibleTimeRequest.getPlanId(), userId, possibleTimeRequest.getPossibleDate(), possibleTimeRequest.getPossibleTime());

        planDao.saveTime(planMemberTime);
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
            String[] membersName = new String[24];
            Arrays.fill(membersName, "");

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
                        membersName[intTime] += userDao.getUserName(memberPossibleTime.getUserId()) + ", ";
                        // [("정소민, "), (""), (""), ... , (""), ("")]  각 시간에 가능한 구성원
                        // [("정소민, 정경은, "), ("정소민, "), ("정소민, 이안진, "), ... , (""), ("정경은, 이안진, ")]  이렇게 채워질 것
                    }
                }

            }

            for(int i=0; i<membersName.length; i++) {
                if(!(membersName[i] == "")) {
                    membersName[i] = membersName[i].trim().substring(0, membersName[i].length()-2);
                    // [("정소민, 정경은"), ("정소민"), ("정소민, 이안진"), ... , (""), ("정경은, 이안진")]
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

                MemberResponse memberResponse = new MemberResponse(i, membersCount[i], membersName[i]);
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


}
