package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.home.HomePlanRequest;
import com.kuit.conet.dto.response.home.HomeDayPlanResponse;
import com.kuit.conet.dto.response.home.HomeMonthPlanResponse;
import com.kuit.conet.dto.response.home.HomeWaitingPlanResponse;
import com.kuit.conet.service.HomeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    /**
     * 홈 - 날짜 (dd)
     * */
    @GetMapping("/month")
    public BaseResponse<HomeMonthPlanResponse> getPlanOnMonth(HttpServletRequest httpRequest, @RequestBody @Valid HomePlanRequest planRequest) {
        HomeMonthPlanResponse response = homeService.getPlanOnMonth(httpRequest, planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 홈 - 날짜(yyyy-MM-dd) / 시각(hh-mm) / 모임 명 / 약속 명
     * - '나'의 직접적인 참여 여부와 무관
     * */
    @GetMapping("/day")
    public BaseResponse<HomeDayPlanResponse> getPlanOnDay(HttpServletRequest httpRequest, @RequestBody @Valid HomePlanRequest planRequest) {
        HomeDayPlanResponse response = homeService.getPlanOnDay(httpRequest, planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 홈 - 날짜(yyyy-MM-dd) / 시각(hh-mm) / 모임 명 / 약속 명
     * - '나'의 직접적인 참여 여부와 무관
     * */
    @GetMapping("/waiting")
    public BaseResponse<HomeWaitingPlanResponse> getWaitingPlan(HttpServletRequest httpRequest) {
        HomeWaitingPlanResponse response = homeService.getWaitingPlanOnDay(httpRequest);
        return new BaseResponse<>(response);
    }
}