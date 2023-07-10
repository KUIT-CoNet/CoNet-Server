package com.kuit.conet.dto.response.team;

import lombok.*;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MakeTeamResponse {

    private Long groupId;
    private String inviteCode;

    private Timestamp codeGeneratedTime;

}
