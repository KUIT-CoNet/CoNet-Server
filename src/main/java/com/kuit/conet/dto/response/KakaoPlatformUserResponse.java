package com.kuit.conet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoPlatformUserResponse {
    private String platformId;
    private String email;
}
