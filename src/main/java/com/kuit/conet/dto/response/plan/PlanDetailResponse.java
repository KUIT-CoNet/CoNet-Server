package com.kuit.conet.dto.response.plan;

import com.kuit.conet.domain.plan.PlanDetail;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanDetailResponse {
    private List<PlanDetail> details;
}