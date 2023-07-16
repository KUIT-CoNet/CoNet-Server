package com.kuit.conet.domain;

import java.util.Arrays;
import java.util.Objects;

import com.kuit.conet.common.exception.PlatformException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_PLATFORM;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Platform {
    APPLE("APPLE"),
    KAKAO("KAKAO");

    private String platform;

    public static Platform from(String platform) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.platform, platform))
                .findFirst()
                .orElseThrow(() -> new PlatformException(INVALID_PLATFORM));
    }
}
