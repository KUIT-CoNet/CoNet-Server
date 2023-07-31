package com.kuit.conet.dto.response.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegenerateCodeResponse {
    private Long teamId;
    private String inviteCode;
    private String codeDeadLine;
}
