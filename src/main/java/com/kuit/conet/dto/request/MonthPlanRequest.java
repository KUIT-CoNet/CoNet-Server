package com.kuit.conet.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonthPlanRequest {
    String searchDate;
    // yyyy-MM
}