package com.kuit.conet.dto.response.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParticipateTeamResponse {
    private String userName;
    private String teamName;
    private Boolean status;
}
