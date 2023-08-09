package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.domain.plan.SideMenuFixedPlan;
import com.kuit.conet.domain.plan.PastPlan;
import com.kuit.conet.domain.plan.PlanDetail;
import com.kuit.conet.dto.request.plan.*;
import com.kuit.conet.dto.request.team.TeamIdRequest;
import com.kuit.conet.dto.response.plan.*;
import com.kuit.conet.service.PlanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public BaseResponse<String> registerTime(HttpServletRequest httpRequest, @RequestBody @Valid PossibleTimeRequest request) {
        planService.saveTime(request, httpRequest);
        return new BaseResponse<>("사용자의 가능한 시간 등록에 성공하였습니다.");
    }

    @GetMapping("/user-time")
    public BaseResponse<UserTimeResponse> getUserTime(HttpServletRequest httpRequest, @ModelAttribute @Valid PlanIdRequest request) {
        UserTimeResponse response = planService.getUserTime(request, httpRequest);
        return new BaseResponse<>(response);
    }

    @GetMapping("/member-time")
    public BaseResponse<MemberPossibleTimeResponse> getMemberTime(@ModelAttribute @Valid PlanIdRequest request) {
        MemberPossibleTimeResponse response = planService.getMemberTime(request);
        return new BaseResponse<>(response);
    }

    @PostMapping("/fix")
    public BaseResponse<String> fixPlan(@RequestBody @Valid FixPlanRequest fixPlanRequest) {
        String response = planService.fixPlan(fixPlanRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 모임 내 특정 달의 약속 조회 - 날짜 (dd)
     * */
    @GetMapping("/month")
    public BaseResponse<MonthPlanResponse> getPlanInMonth(@ModelAttribute @Valid TeamFixedPlanRequest planRequest) {
        MonthPlanResponse response = planService.getPlanInMonth(planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 모임 내 특정 날짜 확정 약속 조회 - 날짜(yyyy-MM-dd) / 시각(hh-mm)
     * - '나'의 직접적인 참여 여부와 무관
     * */
    @GetMapping("/day")
    public BaseResponse<TeamPlanOnDayResponse> getPlanOnDay(@ModelAttribute @Valid TeamFixedPlanRequest planRequest) {
        TeamPlanOnDayResponse response = planService.getPlanOnDay(planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 모임 내 대기 중인 약속 조회 - 날짜(yyyy-MM-dd) / 시각(hh-mm) / 약속 명 / 모임 명
     * - '나'의 직접적인 참여 여부와 무관
     * - 모임 명은 필요 없지만 하나의 dto 를 공유하기 위하여 반환함
     * */
    @GetMapping("/waiting")
    public BaseResponse<WaitingPlanResponse> getWaitingPlan(@ModelAttribute @Valid TeamWaitingPlanRequest planRequest) {
        WaitingPlanResponse response = planService.getWaitingPlan(planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 약속 상세 정보 조회
     * */
    @GetMapping("/detail")
    public BaseResponse<PlanDetail> getPlanDetail(@ModelAttribute @Valid PlanIdRequest planRequest) {
        PlanDetail response = planService.getPlanDetail(planRequest);
        return new BaseResponse<>(response);
    }

    @PostMapping("/delete")
    public BaseResponse<String> deletePlan(@RequestBody @Valid PlanIdRequest planRequest) {
        String response = planService.deletePlan(planRequest);
        return new BaseResponse<>(response);
    }

    @PostMapping("/update-waiting")
    public BaseResponse<String> updateWaitingPlan(@RequestBody @Valid UpdateWaitingPlanRequest planRequest) {
        String response = planService.updateWaitingPlan(planRequest);
        return new BaseResponse<>(response);
    }

    @PostMapping("/update-fixed")
    public BaseResponse<String> updateFixedPlan(@RequestPart(value = "requestBody") @Valid UpdatePlanRequest planRequest, @RequestPart(value = "file", required = false) MultipartFile historyImg) {
        String response = planService.updateFixedPlan(planRequest, historyImg);
        return new BaseResponse<>(response);
    }

    /**
     * 지난 약속 - 모임 내 사이드바 메뉴
     * */
    @GetMapping("/past")
    public BaseResponse<List<PastPlan>> getPastPlan(@ModelAttribute @Valid TeamIdRequest planRequest) {
        List<PastPlan> response = planService.getPastPlan(planRequest);
        return new BaseResponse<>(response);
    }

    /**
     * 확정 약속 - 모임 내 사이드바 메뉴
     * */
    @GetMapping("/fixed")
    public BaseResponse<List<SideMenuFixedPlan>> getFixedPlan(@ModelAttribute @Valid TeamIdRequest planRequest) {
        List<SideMenuFixedPlan> response = planService.getFixedPlan(planRequest);
        return new BaseResponse<>(response);
    }

    @GetMapping("/non-history")
    public BaseResponse<List<PastPlan>> getNotRegisteredToHistoryPlan(@ModelAttribute @Valid TeamIdRequest planRequest) {
        List<PastPlan> response = planService.getNotRegisteredToHistoryPlan(planRequest);
        return new BaseResponse<>(response);
    }

    @GetMapping("/member-plan")
    public BaseResponse<List<MemberIsInPlanResponse>> getMemberIsInPlan(@ModelAttribute @Valid MemberIsInPlanRequest planRequest) {
        List<MemberIsInPlanResponse> responses = planService.getMemberIsInPlan(planRequest);
        return new BaseResponse<>(responses);
    }
}