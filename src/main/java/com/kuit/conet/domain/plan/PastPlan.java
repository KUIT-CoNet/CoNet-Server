package com.kuit.conet.domain.plan;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PastPlan {
    private Long planId;
    private String date;
    private String time;
    private String planName;
    private Boolean isRegisteredToHistory;
}