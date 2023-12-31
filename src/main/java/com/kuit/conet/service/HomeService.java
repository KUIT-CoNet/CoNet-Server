package com.kuit.conet.service;

import com.kuit.conet.dao.HomeDao;
import com.kuit.conet.domain.plan.HomeFixedPlanOnDay;
import com.kuit.conet.domain.plan.WaitingPlan;
import com.kuit.conet.dto.request.plan.HomePlanRequest;
import com.kuit.conet.dto.response.plan.HomePlanOnDayResponse;
import com.kuit.conet.dto.response.plan.MonthPlanResponse;
import com.kuit.conet.dto.response.plan.WaitingPlanResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {
    private final HomeDao homeDao;

    public MonthPlanResponse getPlanInMonth(HttpServletRequest httpRequest, HomePlanRequest planRequest) {
        List<Integer> planDates = new ArrayList<>();

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        //String searchDate = planRequest.getSearchDate(); // yyyy-MM

        List<String> dateList = homeDao.getPlanInMonth(userId, planRequest.getSearchDate());
        for(String tempDate : dateList) {
            Integer date = Integer.parseInt(tempDate.split("-")[2]);
            planDates.add(date);
        }

        return new MonthPlanResponse(planDates.size(), planDates);
    }

    public HomePlanOnDayResponse getPlanOnDay(HttpServletRequest httpRequest, HomePlanRequest planRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        String searchDate = planRequest.getSearchDate(); // yyyy-MM-dd

        List<HomeFixedPlanOnDay> plans = homeDao.getPlanOnDay(userId, searchDate);

        return new HomePlanOnDayResponse(plans.size(), plans);
    }

    public WaitingPlanResponse getWaitingPlan(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        List<WaitingPlan> plans = homeDao.getWaitingPlan(userId);

        return new WaitingPlanResponse(plans.size(), plans);
    }
}