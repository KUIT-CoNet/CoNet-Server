package com.kuit.conet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppleLoginRequest {
    private String idToken;
}

/**
 * id_token Request 객체
 * */