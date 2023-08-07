package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.plan.HomePlanRequest;
import com.kuit.conet.dto.response.plan.HomePlanOnDayResponse;
import com.kuit.conet.dto.response.plan.MonthPlanResponse;
import com.kuit.conet.dto.response.plan.WaitingPlanResponse;
import com.kuit.conet.service.HomeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    /**
     * 홈 특정 달의 확정 약속이 존재하는 날짜 조회 - 날짜 (dd)
     * */
    @GetMapping("/month")
    public BaseResponse<MonthPlanResponse> getPlanInMonth(HttpServletRequest httpRequest, @ModelAttribute @Valid HomePlanRequest planRequest) {
        MonthPlanResponse response = homeService.getPlanInMonth(httpRequest, planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 홈 특정 날짜의 확정 약속 조회 - 날짜(yyyy-MM-dd) / 시각(hh-mm) / 모임 명 / 약속 명
     * - '나'의 직접적인 참여 여부와 무관
     * */
    @GetMapping("/day")
    public BaseResponse<HomePlanOnDayResponse> getPlanOnDay(HttpServletRequest httpRequest, @ModelAttribute @Valid HomePlanRequest planRequest) {
        HomePlanOnDayResponse response = homeService.getPlanOnDay(httpRequest, planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 홈 대기 중인 약속 조회 - 날짜(yyyy-MM-dd) / 시각(hh-mm) / 모임 명 / 약속 명
     * - '나'의 직접적인 참여 여부와 무관
     * */
    @GetMapping("/waiting")
    public BaseResponse<WaitingPlanResponse> getWaitingPlan(HttpServletRequest httpRequest) {
        WaitingPlanResponse response = homeService.getWaitingPlan(httpRequest);
        return new BaseResponse<>(response);
    }
}