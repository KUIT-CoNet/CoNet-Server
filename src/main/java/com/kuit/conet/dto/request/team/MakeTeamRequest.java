package com.kuit.conet.dto.request.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MakeTeamRequest {
    private Long userId;
    private String teamName;
    private String teamImgUrl;
}