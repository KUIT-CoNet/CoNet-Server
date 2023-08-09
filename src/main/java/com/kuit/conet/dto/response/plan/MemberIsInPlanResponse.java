package com.kuit.conet.dto.response.plan;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberIsInPlanResponse {
    private Long userId;
    private String name;
    private String userImgUrl;
    private Boolean isInPlan;

    public MemberIsInPlanResponse(Long userId, String name, String userImgUrl) {
        this.userId = userId;
        this.name = name;
        this.userImgUrl = userImgUrl;
    }
}
