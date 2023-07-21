package com.kuit.conet.service;

import com.kuit.conet.dao.PlanDao;
import com.kuit.conet.domain.Plan;
import com.kuit.conet.domain.PlanMemberTime;
import com.kuit.conet.dto.request.plan.CreatePlanRequest;
import com.kuit.conet.dto.request.plan.PossibleTimeRequest;
import com.kuit.conet.dto.request.plan.UserTimeRequest;
import com.kuit.conet.dto.response.plan.CreatePlanResponse;
import com.kuit.conet.dto.response.plan.UserTimeResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanDao planDao;

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

    public UserTimeResponse getUserTime(UserTimeRequest userTimeRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        if(!planDao.isExistingUserTime(userTimeRequest.getPlanId(), userId)) {
            return new UserTimeResponse(userTimeRequest.getPlanId(), userId, null);
        }

        return planDao.getUserTime(userTimeRequest.getPlanId(), userId);
    }
}
