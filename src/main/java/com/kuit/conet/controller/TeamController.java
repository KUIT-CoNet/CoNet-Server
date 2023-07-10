package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.team.MakeTeamRequest;
import com.kuit.conet.dto.request.team.ParticipateTeamRequest;
import com.kuit.conet.dto.response.team.MakeTeamResponse;
import com.kuit.conet.dto.response.team.ParticipateTeamResponse;
import com.kuit.conet.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;
    @PostMapping("/make-team")
    public BaseResponse<MakeTeamResponse> makeTeam(@RequestBody @Validated MakeTeamRequest request) {
        MakeTeamResponse response = teamService.makeTeam(request);
        return new BaseResponse<MakeTeamResponse>(response);
    }

    @PostMapping("/participation")
    public BaseResponse<ParticipateTeamResponse> participateTeam(@RequestBody @Valid ParticipateTeamRequest participateRequest) {
        ParticipateTeamResponse response = teamService.participateTeam(participateRequest);
        return new BaseResponse<ParticipateTeamResponse>(response);
    }
}
