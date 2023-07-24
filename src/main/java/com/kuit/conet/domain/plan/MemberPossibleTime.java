package com.kuit.conet.domain.plan;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberPossibleTime {
    private Long userId;
    private String possibleTime;
}
