package com.kuit.conet.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoPlatformUserResponse {
    private String platformId;
    private String email;
}
