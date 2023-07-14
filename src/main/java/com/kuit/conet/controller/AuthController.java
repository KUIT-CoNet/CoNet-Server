package com.kuit.conet.controller;

import com.kuit.conet.annotation.ClientIp;
import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.auth.LoginRequest;
import com.kuit.conet.dto.request.auth.PutOptionTermAndNameRequest;
import com.kuit.conet.dto.response.auth.AgreeTermAndPutNameResponse;
import com.kuit.conet.dto.response.auth.LoginResponse;
import com.kuit.conet.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    // 애플 로그인
    @PostMapping("/login/apple")
    public BaseResponse<LoginResponse> loginApple(@RequestBody @Valid LoginRequest loginRequest, @ClientIp String clientIp) {
        LoginResponse response = authService.appleLogin(loginRequest, clientIp);
        return new BaseResponse<LoginResponse>(response);
    }

    // 카카오 로그인
    @PostMapping("/login/kakao")
    public BaseResponse<LoginResponse> loginKakao(@RequestBody @Valid LoginRequest loginRequest, @ClientIp String clientIp) {
        LoginResponse response = authService.kakaoLogin(loginRequest, clientIp);
        return new BaseResponse<LoginResponse>(response);
    }

    @PostMapping("/regenerate-token")
    public BaseResponse<LoginResponse> regenerateToken(HttpServletRequest httpRequest, @ClientIp String clientIp) {
        LoginResponse response = authService.regenerateToken((String) httpRequest.getAttribute("token"), clientIp);
        return new BaseResponse<LoginResponse>(response);
    }

    // 이용 약관 동의 및 이름 입력 DB 업데이트
    @PostMapping("/term-and-name")
    public BaseResponse<AgreeTermAndPutNameResponse> agreeTermAndPutName(@RequestBody @Valid PutOptionTermAndNameRequest nameRequest, HttpServletRequest httpRequest, @ClientIp String clientIp) {
        AgreeTermAndPutNameResponse response = authService.agreeTermAndPutName(nameRequest, httpRequest, clientIp);
        return new BaseResponse<>(response);
    }
}