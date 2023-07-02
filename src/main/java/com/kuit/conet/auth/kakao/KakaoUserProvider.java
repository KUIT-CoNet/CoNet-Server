package com.kuit.conet.auth.kakao;

import com.kuit.conet.common.exception.InvalidTokenException;
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
    private final KakaoOidcParser kakaoOidcParser;
    private final KakaoPublicKeyGenerator publicKeyGenerator;

    private final KakaoClient kakaoClient;
    @Value("${oauth.kakao.iss}")
    private String iss;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    public KakaoPlatformUserResponse getPayloadFromIdToken(String identityToken) {
        Map<String, String> headers = kakaoOidcParser.parseHeaders(identityToken);
        KakaoPublicKeys kakaoPublicKeys = kakaoClient.getKakaoOIDCOpenKeys();
        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, kakaoPublicKeys);

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