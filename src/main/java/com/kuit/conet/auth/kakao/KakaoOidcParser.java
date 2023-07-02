package com.kuit.conet.auth.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.common.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Component
@RequiredArgsConstructor
public class KakaoOidcParser {
    private final String KID = "kid";
    private static final String IDENTITY_TOKEN_SPLITER = "\\.";
    private static final int POSITIVE_SIGN_NUMBER = 1;
    private static final String PUBLIC_KEY_ALGORITHM = "RSA";

    public String getKidFromTokenHeader(String token, String iss, String clientId) {
        return (String) getTokenClaims(token, iss, clientId).getHeader().get(KID);
    }

    private Jwt<Header, Claims> getTokenClaims(String token, String iss, String clientId) {
        try {
            return Jwts.parserBuilder()
                    .requireAudience(clientId)
                    .requireIssuer(iss)
                    .build()
                    .parseClaimsJwt(getToken(token));
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException(INVALID_TOKEN);
        } catch (Exception e) {
            throw new InvalidTokenException(MALFORMED_TOKEN);
        }
    }

    private String getToken(String token) {
        String[] splitToken = token.split(IDENTITY_TOKEN_SPLITER);
        try {
            return splitToken[0] + "." + splitToken[1] + ".";
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidTokenException(UNSUPPORTED_TOKEN_TYPE_FOR_KAKAO);
        }
    }

    public Claims getOIDCClaims(String identityToken, KakaoPublicKey publicKey) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(publicKey))
                    .build()
                    .parseClaimsJws(identityToken).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException(INVALID_TOKEN);
        } catch (Exception e) {
            throw new InvalidTokenException(MALFORMED_TOKEN);
        }
    }

    private Key getRSAPublicKey(KakaoPublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodeN = Base64.getUrlDecoder().decode(publicKey.getN());
        byte[] decodeE = Base64.getUrlDecoder().decode(publicKey.getE());

        BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, decodeN);
        BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, decodeE);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(PUBLIC_KEY_ALGORITHM);
            return keyFactory.generatePublic(publicKeySpec);
        }catch (NoSuchAlgorithmException | InvalidKeySpecException exception){
            throw new IllegalStateException("Apple OAuth 로그인 중 public key 생성에 문제가 발생했습니다.");
        }

    }
}
