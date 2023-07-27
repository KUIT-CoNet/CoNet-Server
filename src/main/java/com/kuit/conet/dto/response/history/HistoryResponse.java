package com.kuit.conet.dto.response.history;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HistoryResponse {
    private Long planId;
    private String planName;
    private String planDate;
    private int planMemberNum;
    private String historyImgUrl;
    private String historyDescription;
}