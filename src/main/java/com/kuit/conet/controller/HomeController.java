package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.MonthPlanRequest;
import com.kuit.conet.dto.response.MonthPlanResponse;
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

    @GetMapping("/month")
    public BaseResponse<MonthPlanResponse> getPlanOnMonth(HttpServletRequest httpRequest, @RequestBody @Valid MonthPlanRequest planRequest) {
        MonthPlanResponse response = homeService.getPlanOnMonth(httpRequest, planRequest);
        return new BaseResponse<>(response);
    }
}