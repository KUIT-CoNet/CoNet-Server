package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.plan.*;
import com.kuit.conet.dto.response.plan.MonthPlanResponse;
import com.kuit.conet.dto.response.plan.CreatePlanResponse;
import com.kuit.conet.dto.response.plan.MemberPossibleTimeResponse;
import com.kuit.conet.dto.response.plan.UserTimeResponse;
import com.kuit.conet.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team/plan")
public class PlanController {
    private final PlanService planService;

    @PostMapping("/create")
    public BaseResponse<CreatePlanResponse> createPlan(@RequestBody @Valid CreatePlanRequest request) {
        CreatePlanResponse response = planService.createPlan(request);
        return new BaseResponse<>(response);
    }

    @PostMapping("/time")
    public BaseResponse<String> registerTime(@RequestBody @Valid PossibleTimeRequest request, HttpServletRequest httpRequest) {
        planService.saveTime(request, httpRequest);
        return new BaseResponse<>("사용자의 가능한 시간 등록에 성공하였습니다.");
    }

    @GetMapping("/user-time")
    public BaseResponse<UserTimeResponse> getUserTime(@RequestBody @Valid PlanIdRequest request, HttpServletRequest httpRequest) {
        UserTimeResponse response = planService.getUserTime(request, httpRequest);
        return new BaseResponse<>(response);
    }

    @GetMapping("/member-time")
    public BaseResponse<MemberPossibleTimeResponse> getMemberTime(@RequestBody @Valid PlanIdRequest request) {
        MemberPossibleTimeResponse response = planService.getMemberTime(request);
        return new BaseResponse<>(response);
    }

    @PostMapping("/fix")
    public BaseResponse<String> fixPlan(@RequestBody @Valid FixPlanRequest fixPlanRequest) {
        String response = planService.fixPlan(fixPlanRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 모임 내 약속 - 날짜 (dd)
     * */
    @GetMapping("/month")
    public BaseResponse<MonthPlanResponse> getPlanInMonth(@RequestBody @Valid TeamFixedPlanRequest planRequest) {
        MonthPlanResponse response = planService.getPlanInMonth(planRequest);
        return new BaseResponse<>(response);
    }
}
