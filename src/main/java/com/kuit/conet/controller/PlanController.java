package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.plan.CreatePlanRequest;
import com.kuit.conet.dto.response.plan.CreatePlanResponse;
import com.kuit.conet.service.PlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanService planService;

    @PostMapping("/create")
    public BaseResponse<CreatePlanResponse> createPlan(@RequestBody @Valid CreatePlanRequest request) {
        CreatePlanResponse response = planService.createPlan(request);
        return new BaseResponse<CreatePlanResponse>(response);
    }


}
