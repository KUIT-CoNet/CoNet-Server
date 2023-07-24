package com.kuit.conet.domain.plan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FixedPlan {
    private String date; // yyyy-MM-dd
    private String time; // hh-mm
    private String teamName;
    private String planName;
}
/*
 * 날짜 / 시각 / 모임 명 / 약속 명
 * */