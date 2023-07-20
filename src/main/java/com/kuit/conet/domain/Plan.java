package com.kuit.conet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
public class Plan {
    private Long planId;
    private Long teamId;
    private String planName;
    private Date planStartPeriod;
    private Date planEndPeriod;
    private Date fixedDate;
    private Time fixedTime;
    private Boolean status;

    public Plan(Long teamId, String planName, Date planStartPeriod, Date planEndPeriod) {
        this.teamId = teamId;
        this.planName = planName;
        this.planStartPeriod = planStartPeriod;
        this.planEndPeriod = planEndPeriod;
    }
}
