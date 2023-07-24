package com.kuit.conet.domain.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanMemberTime {
    private Long planId;
    private Long userId;
    private Date possibleDate;
    private String possibleTime;
}
