package com.kuit.conet.dto.response.home;

import com.kuit.conet.domain.FixedPlan;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HomeDayPlanResponse {
    int count;
    List<FixedPlan> plans;
}