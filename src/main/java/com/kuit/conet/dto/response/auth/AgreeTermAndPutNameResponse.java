package com.kuit.conet.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AgreeTermAndPutNameResponse {
    private String name;
    private String email;
    private Boolean serviceTerm;
    private Boolean optionTerm;
}
