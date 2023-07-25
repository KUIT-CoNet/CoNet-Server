package com.kuit.conet.dto.request.history;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HistoryRegisterRequest {
    private Long planId;
    private String imgUrl;
    private String description;
}