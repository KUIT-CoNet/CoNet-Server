package com.kuit.conet.dto.response.team;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetTeamMemberResponse {
    private List<Long> userId;
    private List<String> userName;
}
