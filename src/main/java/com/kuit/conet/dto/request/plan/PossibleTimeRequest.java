package com.kuit.conet.dto.request.plan;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class PossibleTimeRequest {
    private Long planId;
    private Date possibleDate;
    private List<Integer> possibleTime;
}
