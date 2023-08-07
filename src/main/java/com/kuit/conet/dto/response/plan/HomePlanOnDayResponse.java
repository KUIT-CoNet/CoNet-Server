package com.kuit.conet.dto.response.plan;

import com.kuit.conet.domain.plan.HomeFixedPlanOnDay;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HomePlanOnDayResponse {
    int count;
    List<HomeFixedPlanOnDay> plans;
}