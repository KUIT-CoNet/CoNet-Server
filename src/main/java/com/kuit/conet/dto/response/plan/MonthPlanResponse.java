package com.kuit.conet.dto.response.plan;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonthPlanResponse {
    int count;
    List<Integer> dates;
}
