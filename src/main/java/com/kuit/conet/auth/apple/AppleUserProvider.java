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
public class AppleUserProvider {
    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final ApplePublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public ApplePlatformUserResponse getApplePlatformUser(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);

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
