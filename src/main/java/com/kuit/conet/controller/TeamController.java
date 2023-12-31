package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.team.CreateTeamRequest;
import com.kuit.conet.dto.request.team.ParticipateTeamRequest;
import com.kuit.conet.dto.request.team.TeamIdRequest;
import com.kuit.conet.dto.request.team.UpdateTeamRequest;
import com.kuit.conet.dto.response.StorageImgResponse;
import com.kuit.conet.dto.response.team.*;
import com.kuit.conet.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;
    @PostMapping("/create")
    public BaseResponse<CreateTeamResponse> createTeam(@RequestPart(value = "request") @Valid CreateTeamRequest createTeamRequest, HttpServletRequest httpRequest, @RequestParam(value = "file") MultipartFile file) {
        CreateTeamResponse response = teamService.createTeam(createTeamRequest, httpRequest, file);
        return new BaseResponse<CreateTeamResponse>(response);
    }

    @PostMapping("/participate")
    public BaseResponse<ParticipateTeamResponse> participateTeam(@RequestBody @Valid ParticipateTeamRequest participateRequest, HttpServletRequest httpRequest) {
        ParticipateTeamResponse response = teamService.participateTeam(participateRequest, httpRequest);
        return new BaseResponse<ParticipateTeamResponse>(response);
    }

    @PostMapping("/code")
    public BaseResponse<RegenerateCodeResponse> regenerateCode(@RequestBody @Valid TeamIdRequest request) {
        RegenerateCodeResponse response = teamService.regenerateCode(request);
        return new BaseResponse<>(response);
    }

    @GetMapping
    public BaseResponse<List<GetTeamResponse>> getTeam(HttpServletRequest httpRequest) {
        List<GetTeamResponse> responses = teamService.getTeam(httpRequest);
        return new BaseResponse<List<GetTeamResponse>>(responses);
    }

    @PostMapping("/leave")
    public BaseResponse<String> leaveTeam(@RequestBody @Valid TeamIdRequest request, HttpServletRequest httpRequest) {
        String response = teamService.leaveTeam(request, httpRequest);
        return new BaseResponse<String>(response);
    }

    @PostMapping("/delete")
    public BaseResponse<String> deleteTeam(@RequestBody @Valid TeamIdRequest request) {
        String response = teamService.deleteTeam(request);
        return new BaseResponse<String>(response);
    }

    @PostMapping("/update")
    public BaseResponse<StorageImgResponse> updateTeam(@RequestPart(value = "request") @Valid UpdateTeamRequest updateTeamRequest, @RequestParam(value = "file") MultipartFile file) {
        StorageImgResponse response = teamService.updateTeam(updateTeamRequest, file);
        return new BaseResponse<StorageImgResponse>(response);
    }

    @GetMapping("/members")
    public BaseResponse<List<GetTeamMemberResponse>> getTeamMembers(@ModelAttribute @Valid TeamIdRequest request) {
        List<GetTeamMemberResponse> response = teamService.getTeamMembers(request);
        return new BaseResponse<>(response);
    }

    @PostMapping("/bookmark")
    public BaseResponse<String> bookmarkTeam(HttpServletRequest httpRequest, @RequestBody @Valid TeamIdRequest request) {
        teamService.bookmarkTeam(httpRequest, request);
        return new BaseResponse<>("모임을 즐겨찾기에 추가하였습니다.");
    }

    @PostMapping("/bookmark/delete")
    public BaseResponse<String> unBookmarkTeam(HttpServletRequest httpRequest, @RequestBody @Valid TeamIdRequest request) {
        teamService.unBookmarkTeam(httpRequest, request);
        return new BaseResponse<>("모임을 즐겨찾기에서 삭제하였습니다.");
    }

    @GetMapping("/detail")
    public BaseResponse<GetTeamResponse> getTeamDetail(HttpServletRequest httpRequest, @ModelAttribute @Valid TeamIdRequest request) {
        GetTeamResponse response = teamService.getTeamDetail(httpRequest, request);
        return new BaseResponse<>(response);
    }

    @GetMapping("/bookmark")
    public BaseResponse<List<GetTeamResponse>> getBookmark(HttpServletRequest httpRequest) {
        List<GetTeamResponse> responses = teamService.getBookmarks(httpRequest);
        return new BaseResponse<>(responses);
    }
}
