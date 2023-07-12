package com.kuit.conet.dto.request.team;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class CreateTeamRequest {
    private String teamName;
    private String teamImgUrl;
}