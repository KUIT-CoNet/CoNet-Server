package com.kuit.conet.auth.kakao;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoPublicKey {
    private String kid;
    private String alg;
    private String use;
    private String n;
    private String e;
}
