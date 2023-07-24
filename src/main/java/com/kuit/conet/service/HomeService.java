package com.kuit.conet.service;

import com.kuit.conet.dao.HomeDao;
import com.kuit.conet.domain.FixedPlan;
import com.kuit.conet.domain.WaitingPlan;
import com.kuit.conet.dto.request.home.HomePlanRequest;
import com.kuit.conet.dto.response.home.HomeDayPlanResponse;
import com.kuit.conet.dto.response.home.HomeMonthPlanResponse;
import com.kuit.conet.dto.response.home.HomeWaitingPlanResponse;
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

    public HomeMonthPlanResponse getPlanOnMonth(HttpServletRequest httpRequest, HomePlanRequest planRequest) {
        List<Integer> planDates = new ArrayList<>();

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        String searchDate = planRequest.getSearchDate(); // yyyy-MM

        List<String> dateList = homeDao.getPlanOnMonth(userId, searchDate);
        for(String tempDate : dateList) {
            Integer date = Integer.parseInt(tempDate.split("-")[2]);
            planDates.add(date);
        }

        return new HomeMonthPlanResponse(planDates.size(), planDates);
    }

    public HomeDayPlanResponse getPlanOnDay(HttpServletRequest httpRequest, HomePlanRequest planRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        String searchDate = planRequest.getSearchDate(); // yyyy-MM-dd

        List<FixedPlan> plans = homeDao.getPlanOnDay(userId, searchDate);

        return new HomeDayPlanResponse(plans.size(), plans);
    }

    public HomeWaitingPlanResponse getWaitingPlanOnDay(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        List<WaitingPlan> plans = homeDao.getWaitingPlanOnDay(userId);

        return new HomeWaitingPlanResponse(plans.size(), plans);
    }
}