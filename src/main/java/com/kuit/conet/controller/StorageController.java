package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.response.StorageImgResponse;
import com.kuit.conet.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
public class StorageController {
    private final StorageService storageService;

    @PostMapping("/image")
    public BaseResponse<StorageImgResponse> uploadProfileImage(@RequestParam(value = "file") MultipartFile file) throws IOException {
        StorageImgResponse response = storageService.uploadProfileImage(file, 8L);
        return new BaseResponse<>(response);
    }
}