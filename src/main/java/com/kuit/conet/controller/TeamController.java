package com.kuit.conet.controller;

import com.kuit.conet.common.response.BaseResponse;
import com.kuit.conet.dto.request.team.MakeTeamRequest;
import com.kuit.conet.dto.request.team.RegenerateCodeRequest;
import com.kuit.conet.dto.response.team.MakeTeamResponse;
import com.kuit.conet.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;
    @PostMapping("/make")
    public BaseResponse<MakeTeamResponse> makeTeam(@RequestBody @Validated MakeTeamRequest request) {
        MakeTeamResponse response = teamService.makeTeam(request);
        return new BaseResponse<MakeTeamResponse>(response);
    }

    @PostMapping("/code")
    public BaseResponse<MakeTeamResponse> makeTeam(@RequestBody @Validated RegenerateCodeRequest request) {
        MakeTeamResponse response = teamService.regenerateCode(request);
        return new BaseResponse<MakeTeamResponse>(response);
    }
}
