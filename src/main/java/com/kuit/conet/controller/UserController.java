package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.response.user.UserResponse;
import com.kuit.conet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/delete")
    public BaseResponse<String> userDelete(HttpServletRequest httpRequest) {
        userService.userDelete(httpRequest);
        return new BaseResponse<>("유저 삭제에 성공하였습니다.");
    }

    @GetMapping
    public BaseResponse<UserResponse> getUser(HttpServletRequest httpRequest) {
        UserResponse response = userService.getUser(httpRequest);
        return new BaseResponse<>(response);
    }
}