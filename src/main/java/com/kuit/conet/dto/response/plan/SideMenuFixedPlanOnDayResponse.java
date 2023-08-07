package com.kuit.conet.dto.response.plan;

import com.kuit.conet.domain.plan.SideMenuFixedPlan;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SideMenuFixedPlanOnDayResponse {
    int count;
    List<SideMenuFixedPlan> plans;
}