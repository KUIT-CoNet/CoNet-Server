package com.kuit.conet.controller;

import com.kuit.conet.dto.request.AppleLoginRequest;
import com.kuit.conet.dto.response.OAuthTokenResponse;
import com.kuit.conet.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class AuthController {
    private AuthService authService;

    // 애플 로그인
    @PostMapping("/apple")
    public ResponseEntity<OAuthTokenResponse> loginApple(@RequestBody @Valid AppleLoginRequest loginRequest) {
        OAuthTokenResponse response = authService.appleOAuthLogin(loginRequest);
        return ResponseEntity.ok(response);
    }
}