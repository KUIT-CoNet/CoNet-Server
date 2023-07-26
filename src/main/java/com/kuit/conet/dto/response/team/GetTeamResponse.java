package com.kuit.conet.dto.response.team;

import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetTeamResponse {
    private Long teamId;
    private String teamName;
    private String teamImgUrl;
    private Long teamMemberCount;
    private Boolean isNew;

    public GetTeamResponse(Long teamId, String teamName, String teamImgUrl, Boolean isNew) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamImgUrl = teamImgUrl;
        this.isNew = isNew;
    }
}