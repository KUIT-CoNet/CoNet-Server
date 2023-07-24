package com.kuit.conet.dto.request.plan;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanRequest {
    String searchDate;
    // 특정 달의 조회: "yyyy-MM"
    // 특정 날짜 조회: "yyyy-MM-dd"
}