package com.kuit.conet.dto.request.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateWaitingPlanRequest {
    private Long planId;
    private String planName;
}
