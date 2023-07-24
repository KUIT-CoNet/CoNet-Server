package com.kuit.conet.dto.request.plan;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class FixPlanRequest {
    private Long planId;
    private Date fixed_date;
    private Long fixed_time;
    private List<Long> userId;
}
