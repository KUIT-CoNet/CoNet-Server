package com.kuit.conet.auth.apple;

import com.kuit.conet.auth.CoNetPublicKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ApplePublicKeys {
    private List<CoNetPublicKey> keys;

    public CoNetPublicKey getMatchesKey(String alg, String kid) {
        return this.keys
                .stream()
                .filter(k -> k.getAlg().equals(alg) && k.getKid().equals(kid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Apple JWT 값의 alg, kid 정보가 올바르지 않습니다."));
    }
}
/**
 * 일종의 DTO 클래스
 * 필드명이 keys 아닌 다른 값이면 응답을 받아 올 수 없다.
 * */