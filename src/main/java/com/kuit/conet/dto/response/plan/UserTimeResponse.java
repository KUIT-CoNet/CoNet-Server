package com.kuit.conet.dto.response.plan;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserTimeResponse {
    private Long planId;
    private Long userId;
    private Boolean hasRegisteredTime;
    private Boolean hasPossibleTime;
    private List<UserPossibleTimeResponse> possibleTime;
}
