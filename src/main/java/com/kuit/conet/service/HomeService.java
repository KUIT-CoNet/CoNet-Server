package com.kuit.conet.service;

import com.kuit.conet.dao.HomeDao;
import com.kuit.conet.domain.FixedPlan;
import com.kuit.conet.dto.request.PlanRequest;
import com.kuit.conet.dto.response.DayPlanResponse;
import com.kuit.conet.dto.response.MonthPlanResponse;
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

    public MonthPlanResponse getPlanOnMonth(HttpServletRequest httpRequest, PlanRequest planRequest) {
        List<Integer> planDates = new ArrayList<>();

        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        String searchDate = planRequest.getSearchDate(); // yyyy-MM

        List<String> dateList = homeDao.getPlanOnMonth(userId, searchDate);
        for(String tempDate : dateList) {
            Integer date = Integer.parseInt(tempDate.split("-")[2]);
            planDates.add(date);
        }

        return new MonthPlanResponse(planDates.size(), planDates);
    }

    public DayPlanResponse getPlanOnDay(HttpServletRequest httpRequest, PlanRequest planRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        String searchDate = planRequest.getSearchDate(); // yyyy-MM-dd

        List<FixedPlan> plans = homeDao.getPlanOnDay(userId, searchDate);

        return new DayPlanResponse(plans.size(), plans);
    }
}