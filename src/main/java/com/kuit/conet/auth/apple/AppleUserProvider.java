package com.kuit.conet.auth.apple;

import com.kuit.conet.utils.JwtParser;
import com.kuit.conet.utils.auth.PublicKeyGenerator;
import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.dto.response.auth.ApplePlatformUserResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_CLAIMS;

@Component
@RequiredArgsConstructor
public class AppleUserProvider {
    private final JwtParser jwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public ApplePlatformUserResponse getApplePlatformUser(String identityToken) {
        Map<String, String> headers = jwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        PublicKey publicKey = publicKeyGenerator.generateApplePublicKey(headers, applePublicKeys);

        Claims claims = jwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
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
