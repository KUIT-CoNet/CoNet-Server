package com.kuit.conet.auth.kakao;

import com.kuit.conet.auth.apple.ApplePublicKey;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KakaoPublicKeys {
    private List<KakaoPublicKey> keys;

    public KakaoPublicKey getMatchesKey(String kid) {
        return this.keys.stream()
                .filter(o -> o.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Kakao JWT 값의 kid 정보가 올바르지 않습니다."));
    }
}
