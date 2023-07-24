package com.kuit.conet.dto.request.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TeamFixedPlanRequest {
    private Long teamId;
    private String searchDate;
    // 특정 달의 조회: "yyyy-MM"
    // 특정 날짜 조회: "yyyy-MM-dd"
}