package com.kuit.conet.dto.response.team;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GetTeamResponse {
    private String team_name;
    private String team_image_url;
}