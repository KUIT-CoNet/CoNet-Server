package com.kuit.conet.dto.request.team;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParticipateTeamRequest {
    private String inviteCode;
}