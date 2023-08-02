package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.history.HistoryRegisterRequest;
import com.kuit.conet.dto.request.team.TeamIdRequest;
import com.kuit.conet.dto.response.history.HistoryRegisterResponse;
import com.kuit.conet.dto.response.history.HistoryResponse;
import com.kuit.conet.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/history")
public class HistoryController {
    private final HistoryService historyService;

    @PostMapping("/register")
    public BaseResponse<HistoryRegisterResponse> registerToHistory(@RequestPart(value = "registerRequest") @Valid HistoryRegisterRequest registerRequest, @RequestPart(value = "file", required = false) MultipartFile historyImg) {
        HistoryRegisterResponse response = historyService.registerToHistory(registerRequest, historyImg);
        return new BaseResponse<>(response);
    }

    @GetMapping
    public BaseResponse<List<HistoryResponse>> getHistory(@ModelAttribute TeamIdRequest request) {
        List<HistoryResponse> response = historyService.getHistory(request);
        return new BaseResponse<>(response);
    }
}