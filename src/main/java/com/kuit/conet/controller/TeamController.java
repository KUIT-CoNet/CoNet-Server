package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.team.CreateTeamRequest;
import com.kuit.conet.dto.request.team.ParticipateTeamRequest;
import com.kuit.conet.dto.request.team.TeamIdRequest;
import com.kuit.conet.dto.response.team.CreateTeamResponse;
import com.kuit.conet.dto.response.team.LeaveTeamResponse;
import com.kuit.conet.dto.response.team.ParticipateTeamResponse;
import com.kuit.conet.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;
    @PostMapping("/create")
    public BaseResponse<CreateTeamResponse> createTeam(@RequestBody @Valid CreateTeamRequest createTeamRequest, HttpServletRequest httpRequest) {
        CreateTeamResponse response = teamService.createTeam(createTeamRequest, httpRequest);
        return new BaseResponse<CreateTeamResponse>(response);
    }

    @PostMapping("/participate")
    public BaseResponse<ParticipateTeamResponse> participateTeam(@RequestBody @Valid ParticipateTeamRequest participateRequest, HttpServletRequest httpRequest) {
        ParticipateTeamResponse response = teamService.participateTeam(participateRequest, httpRequest);
        return new BaseResponse<ParticipateTeamResponse>(response);
    }

    @PostMapping("/code")
    public BaseResponse<CreateTeamResponse> createTeam(@RequestBody @Valid TeamIdRequest request) {
        CreateTeamResponse response = teamService.regenerateCode(request);
        return new BaseResponse<CreateTeamResponse>(response);
    }

    @PostMapping("/leave")
    public BaseResponse<LeaveTeamResponse> leaveTeam(@RequestBody @Valid TeamIdRequest request, HttpServletRequest httpRequest) {
        LeaveTeamResponse response = teamService.leaveTeam(request, httpRequest);
        return new BaseResponse<LeaveTeamResponse>(response);
    }
}
