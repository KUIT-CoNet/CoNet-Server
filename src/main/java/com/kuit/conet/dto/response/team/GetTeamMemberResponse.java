package com.kuit.conet.dto.response.team;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetTeamMemberResponse {
    private Long userId;
    private String name;
    private String userImgUrl;
}
