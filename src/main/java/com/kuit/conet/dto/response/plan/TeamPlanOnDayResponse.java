package com.kuit.conet.dto.response.plan;

import com.kuit.conet.domain.plan.HomeFixedPlanOnDay;
import com.kuit.conet.domain.plan.TeamFixedPlanOnDay;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamPlanOnDayResponse {
    int count;
    List<TeamFixedPlanOnDay> plans;
}