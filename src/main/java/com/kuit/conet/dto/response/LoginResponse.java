package com.kuit.conet.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class LoginResponse {
    private String email;
    private String accessToken;
    private String refreshToken;
    private Boolean isRegistered;

    public LoginResponse(String email, String accessToken, String refreshToken, Boolean isRegistered) {
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isRegistered = isRegistered;
    }
}
