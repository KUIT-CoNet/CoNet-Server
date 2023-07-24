package com.kuit.conet.dto.response.plan;

import com.kuit.conet.domain.FixedPlan;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DayPlanResponse {
    int count;
    List<FixedPlan> plans;
}