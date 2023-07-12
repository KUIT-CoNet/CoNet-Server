package com.kuit.conet.dto.request.team;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParticipateTeamRequest {
    // private String token;
    private String inviteCode;
}