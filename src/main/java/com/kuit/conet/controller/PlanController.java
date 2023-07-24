package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.plan.*;
import com.kuit.conet.dto.response.plan.*;
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

    /**
     * 모임 내 약속 - 날짜(yyyy-MM-dd) / 시각(hh-mm) / 약속 명
     * - '나'의 직접적인 참여 여부와 무관
     * - 모임 명은 필요 없지만 하나의 dto 를 공유하기 위하여 반환함
     * */
    @GetMapping("/day")
    public BaseResponse<DayPlanResponse> getPlanOnDay(@RequestBody @Valid TeamFixedPlanRequest planRequest) {
        DayPlanResponse response = planService.getPlanOnDay(planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 모임 내 약속 - 날짜(yyyy-MM-dd) / 시각(hh-mm) / 약속 명
     * - '나'의 직접적인 참여 여부와 무관
     * - 모임 명은 필요 없지만 하나의 dto 를 공유하기 위하여 반환함
     * */
    @GetMapping("/waiting")
    public BaseResponse<WaitingPlanResponse> getWaitingPlan(@RequestBody @Valid TeamWaitingPlanRequest planRequest) {
        WaitingPlanResponse response = planService.getWaitingPlan(planRequest);
        return new BaseResponse<>(response);
    }
}
