package com.kuit.conet.dto.request.team;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class CreateTeamRequest {
    // TODO: userId -> accessToken
    // private String accessToken;
    private String teamName;
    private String teamImgUrl;
}