package com.kuit.conet.dto.response.user;

import com.kuit.conet.domain.auth.Platform;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponse {
    private String name;
    private String email;
    private String userImgUrl;
    private Platform platform;
}