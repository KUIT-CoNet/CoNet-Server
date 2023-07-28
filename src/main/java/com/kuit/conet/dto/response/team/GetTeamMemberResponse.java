package com.kuit.conet.dto.response.team;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetTeamMemberResponse {
    private Long userId;
    private String userName;
}
