package com.kuit.conet.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long userId;
    //private String name;
    private String email;
    private String password;
    // private String nickname;
    private Platform platform;
    private String platformId;

    // TODO: 생성자 작성 (기능 구현에 따라 필요한 경우)
    public User(String email, Platform platform, String platformId) {
        this.email = email;
        this.platform = platform;
        this.platformId = platformId;
    }
}
