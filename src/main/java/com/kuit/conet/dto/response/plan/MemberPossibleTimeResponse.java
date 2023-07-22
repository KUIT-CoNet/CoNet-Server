package com.kuit.conet.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberPossibleTimeResponse {
    private Long teamId;
    private Long planId;
    private String planName;
    private Date planStartPeriod;
    private Date planEndPeriod;
    private List<MemberDateTimeResponse> possibleMemberDateTime;
}
