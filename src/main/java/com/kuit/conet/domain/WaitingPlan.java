package com.kuit.conet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WaitingPlan {
    private String startDate; // yyyy-MM-dd
    private String endDate; // yyyy-MM-dd
    private String teamName;
    private String planName;
}
/**
 * 시작 날짜 / 마감 날짜 / 모임 명 / 약속 명
 * */