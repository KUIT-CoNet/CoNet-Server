package com.kuit.conet.dto.request.plan;

import com.kuit.conet.domain.plan.PossibleDateTime;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class PossibleTimeRequest {
    private Long planId;
    //private Boolean hasPossibleTime;
    private List<PossibleDateTime> possibleDateTimes; // 7개 날짜에 대한
}
