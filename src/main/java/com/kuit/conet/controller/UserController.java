package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.TokenRequest;
import com.kuit.conet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/delete")
    public BaseResponse<String> userDelete(@RequestBody @Valid TokenRequest tokenRequest) {
        userService.userDelete(tokenRequest);
        return new BaseResponse<>("유저 삭제에 성공하였습니다.");
    }
}