package com.kuit.conet.domain.team;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class Team {
    private Long teamId;
    private String teamName;
    private String teamImgUrl;
    private String inviteCode;
    private Timestamp codeGeneratedTime;
    private Boolean status;
    private Timestamp createdAt;
    private Boolean isNew;

    public Team(String teamName, String teamImgUrl, String inviteCode, Timestamp codeGeneratedTime) {
        this.teamName = teamName;
        this.teamImgUrl = teamImgUrl;
        this.inviteCode = inviteCode;
        this.codeGeneratedTime = codeGeneratedTime;
    }

    public Team(Long teamId, String teamName, String teamImgUrl, Timestamp createdAt, Boolean isNew) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamImgUrl = teamImgUrl;
        this.createdAt = createdAt;
        this.isNew = isNew;
    }
 }
