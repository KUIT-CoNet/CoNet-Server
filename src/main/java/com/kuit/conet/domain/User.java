package com.kuit.conet.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long userId;
    private String name;
    private String email;
    private String password;
    // private String nickname;
    private Platform platform;
    private String platformId;

    // TODO: 생성자 작성
    public User(String email, Platform platform, String platformId) {
        this.email = email;
        this.platform = platform;
        this.platformId = platformId;
    }
}
