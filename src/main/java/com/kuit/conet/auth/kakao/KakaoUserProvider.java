package com.kuit.conet.auth.kakao;

import com.kuit.conet.utils.JwtParser;
import com.kuit.conet.utils.auth.PublicKeyGenerator;
import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.dto.response.auth.KakaoPlatformUserResponse;
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
    private final JwtParser jwtParser;
    private final PublicKeyGenerator publicKeyGenerator;

    private final KakaoClient kakaoClient;
    @Value("${oauth.kakao.iss}")
    private String iss;

    @Value("${oauth.kakao.client-id}")
    private String clientId;

    public KakaoPlatformUserResponse getPayloadFromIdToken(String identityToken) {
        Map<String, String> headers = jwtParser.parseHeaders(identityToken);
        KakaoPublicKeys kakaoPublicKeys = kakaoClient.getKakaoOIDCOpenKeys();
        PublicKey publicKey = publicKeyGenerator.generateKakaoPublicKey(headers, kakaoPublicKeys);

        Claims claims = jwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);

        return new KakaoPlatformUserResponse(claims.getSubject(), claims.get("email", String.class));
    }
    private void validateClaims(Claims claims) {
        if (!claims.getIssuer().contains(iss) && claims.getAudience().equals(clientId)) {
            throw new InvalidTokenException(INVALID_CLAIMS);
        }
    }
}