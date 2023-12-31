package com.kuit.conet.dto.request.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PutOptionTermAndNameRequest {
    private String name;
    private Boolean optionTerm; // 선택 약관이 1개 -> 0이면 동의 X, 1이면 동의 O
}
