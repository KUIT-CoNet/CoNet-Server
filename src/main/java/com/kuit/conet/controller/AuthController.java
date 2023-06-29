package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.AppleLoginRequest;
import com.kuit.conet.dto.response.LoginResponse;
import com.kuit.conet.service.AuthService;
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
    @ResponseBody
    @PostMapping("/apple")
    public BaseResponse<LoginResponse> loginApple(@RequestBody @Valid AppleLoginRequest loginRequest) {
        LoginResponse response = authService.appleOAuthLogin(loginRequest);
        return new BaseResponse<LoginResponse>(response);
    }
}