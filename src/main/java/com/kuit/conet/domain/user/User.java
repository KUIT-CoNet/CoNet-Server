package com.kuit.conet.domain.user;

import com.kuit.conet.domain.auth.Platform;
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
    private String userImgUrl;
    private Boolean serviceTerm;
    private Boolean optionTerm; // 1: 선택 약관 동의 완료
    private Platform platform;
    private String platformId;

    public User(String email, Platform platform, String platformId) {
        this.email = email;
        this.platform = platform;
        this.platformId = platformId;
    }
}
