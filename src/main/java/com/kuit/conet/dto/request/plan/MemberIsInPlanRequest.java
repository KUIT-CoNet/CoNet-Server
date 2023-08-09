package com.kuit.conet.dto.request.plan;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class MemberIsInPlanRequest {
    private Long teamId;
    private Long planId;
}
