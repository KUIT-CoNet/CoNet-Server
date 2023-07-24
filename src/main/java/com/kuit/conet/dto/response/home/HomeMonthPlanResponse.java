package com.kuit.conet.dto.response.home;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HomeMonthPlanResponse {
    int count;
    List<Integer> dates;
}
