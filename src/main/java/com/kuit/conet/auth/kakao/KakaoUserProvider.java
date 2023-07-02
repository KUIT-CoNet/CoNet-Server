package com.kuit.conet.auth.kakao;

import com.kuit.conet.auth.apple.ApplePublicKeys;
import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.dto.response.ApplePlatformUserResponse;
import com.kuit.conet.dto.response.KakaoPlatformUserResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_CLAIMS;

@Component
@RequiredArgsConstructor
public class KakaoUserProvider {
    private final KakaoClient kakaoClient;
    @Value("${oauth.kakao.iss}")
    private String iss;

    private final KakaoOidcParser kakaoOidcParser;
    @Value("${oauth.kakao.client-id}")
    private String clientId;

    // kid를 토큰에서 가져온다.
    private String getKidFromIdToken(String identityToken, String iss, String clientId) {
        return kakaoOidcParser.getKidFromTokenHeader(identityToken, iss, clientId);
    }

    public KakaoPlatformUserResponse getPayloadFromIdToken(String identityToken) {
        String kid = getKidFromIdToken(identityToken, iss, clientId);
        KakaoPublicKeys kakaoPublicKeys = kakaoClient.getKakaoOIDCOpenKeys();

        KakaoPublicKey publicKey =
                kakaoPublicKeys.getKeys().stream()
                        .filter(o -> o.getKid().equals(kid))
                        .findFirst()
                        .orElseThrow();

        Claims claims = kakaoOidcParser.getOIDCClaims(identityToken, publicKey);
        validateClaims(claims);

        return new KakaoPlatformUserResponse(claims.getSubject(), claims.get("email", String.class));
    }
    private void validateClaims(Claims claims) {
        if (!claims.getIssuer().contains(iss) && claims.getAudience().equals(clientId)) {
            throw new InvalidTokenException(INVALID_CLAIMS);
        }
    }
}