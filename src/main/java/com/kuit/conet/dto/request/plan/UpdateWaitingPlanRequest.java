package com.kuit.conet.dto.request.plan;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateWaitingPlanRequest {
    private Long planId;
    private String planName;
}
