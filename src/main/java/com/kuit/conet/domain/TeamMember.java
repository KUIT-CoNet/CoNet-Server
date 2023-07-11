package com.kuit.conet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TeamMember {
    private Long teamMemberId;
    private Long teamId;
    private Long userId;
    private Boolean status;

    public TeamMember(Long teamId, Long userId) {
        this.teamId = teamId;
        this.userId = userId;
    }
}
