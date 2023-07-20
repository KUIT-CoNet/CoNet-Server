package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.user.NameRequest;
import com.kuit.conet.dto.response.StorageImgResponse;
import com.kuit.conet.dto.response.user.UserResponse;
import com.kuit.conet.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/image")
    public BaseResponse<StorageImgResponse> updateImg(HttpServletRequest httpRequest, @RequestParam(value = "file") MultipartFile file){
        StorageImgResponse response = userService.updateImg(httpRequest, file);
        return new BaseResponse<>(response);
    }

    @PostMapping("/name")
    public BaseResponse<String> updateName(HttpServletRequest httpRequest, @RequestBody @Valid NameRequest nameRequest) {
        userService.updateName(httpRequest, nameRequest);
        return new BaseResponse<>("이름 변경에 성공하였습니다.");
    }
}