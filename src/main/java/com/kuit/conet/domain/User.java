package com.kuit.conet.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long userId;
    private String name;
    private String email;
    private int serviceTerm; // 1: 약관 동의 완료
    private Platform platform;
    private String platformId;

    // TODO: 생성자 작성 (기능 구현에 따라 필요한 경우)
    public User(String email, Platform platform, String platformId) {
        this.email = email;
        this.platform = platform;
        this.platformId = platformId;
    }
}
