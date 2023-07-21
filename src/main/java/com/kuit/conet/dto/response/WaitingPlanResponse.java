package com.kuit.conet.dto.response;

import com.kuit.conet.domain.WaitingPlan;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WaitingPlanResponse {
    private int count;
    private List<WaitingPlan> plans;
}