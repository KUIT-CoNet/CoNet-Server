package com.kuit.conet.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class OAuthTokenResponse {
    private String token;
    private String email;
    private boolean isRegistered;
    private String platformId;
}
