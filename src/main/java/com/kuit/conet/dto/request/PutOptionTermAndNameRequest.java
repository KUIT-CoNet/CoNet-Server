package com.kuit.conet.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PutOptionTermAndNameRequest {
    private String name;
    private Boolean optionTerm; // 선택 약관이 1개 -> 0이면 동의 X, 1이면 동의 O
}
