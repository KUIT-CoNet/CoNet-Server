package com.kuit.conet.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private Long userId;
    private String name;
    private String email;
    private String password;
    // private String nickname;
    private Platform platform;
    private String platformId;

    // TODO: 생성자 작성
}
