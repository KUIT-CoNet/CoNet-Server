package com.kuit.conet.domain.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    private Long teamId;
    private String teamName;
    private String teamImgUrl;
    private String inviteCode;
    private Timestamp codeGeneratedTime;
    private Boolean status;

    public Team(String teamName, String teamImgUrl, String inviteCode, Timestamp codeGeneratedTime) {
        this.teamName = teamName;
        this.teamImgUrl = teamImgUrl;
        this.inviteCode = inviteCode;
        this.codeGeneratedTime = codeGeneratedTime;
    }
 }
