package com.kuit.conet.domain;

import java.util.Arrays;
import java.util.Objects;

import com.kuit.conet.common.exception.PlatformException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Platform {
    APPLE("apple"),
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private String platform;

    public static Platform from(String platform) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.platform, platform))
                .findFirst()
                .orElseThrow(PlatformException::new);
    }
}
