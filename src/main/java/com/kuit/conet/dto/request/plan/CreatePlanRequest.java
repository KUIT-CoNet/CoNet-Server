package com.kuit.conet.dto.request.plan;

import lombok.*;

import java.sql.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class CreatePlanRequest {
    private Long teamId;
    private String planName;
    private Date planStartPeriod;
}
