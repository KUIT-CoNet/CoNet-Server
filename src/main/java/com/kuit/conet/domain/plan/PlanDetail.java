package com.kuit.conet.domain.plan;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanDetail {
    private Long planId;
    private String planName;
    private String date; // yyyy-MM-dd
    private String time; // hh:mm

    // 참여자 정보
    private List<PlanMember> members;

    private Boolean isRegisteredToHistory;
    // 히스토리에 등록되어 있을 때
    private String historyImgUrl;
    private String historyDescription;
}