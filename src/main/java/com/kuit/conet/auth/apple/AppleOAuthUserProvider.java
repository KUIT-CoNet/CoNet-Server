package com.kuit.conet.auth.apple;

import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.dto.response.ApplePlatformUserResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_CLAIMS;

@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {
    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public ApplePlatformUserResponse getApplePlatformUser(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);
        // TODO: claims와 클라이언트로부터 받은 authorization code로 access token / refresh token 받기 -> Response에 포함

        return new ApplePlatformUserResponse(claims.getSubject(), claims.get("email", String.class));
        /*
           claims 의 subject = User domain 의 platformId
         * */
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new InvalidTokenException(INVALID_CLAIMS);
        }
    }
}
