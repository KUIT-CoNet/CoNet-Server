package com.kuit.conet.dto.response.team;

import lombok.*;

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
    private Boolean bookmark;

    public GetTeamResponse(Long teamId, String teamName, String teamImgUrl, Long teamMemberCount) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamImgUrl = teamImgUrl;
        this.teamMemberCount = teamMemberCount;
    }
}