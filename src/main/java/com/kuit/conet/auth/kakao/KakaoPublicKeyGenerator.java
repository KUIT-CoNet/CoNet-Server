package com.kuit.conet.auth.kakao;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class KakaoPublicKeyGenerator {
    private static final String HEADER_KEY_ID = "kid";
    private static final int POSITIVE_SIGN_NUMBER = 1;
    private static final String PUBLIC_KEY_ALGORITHM = "RSA";

    public PublicKey generatePublicKey(Map<String, String> headers, KakaoPublicKeys kakaoPublicKeys) {
        KakaoPublicKey kakaoPublicKey = kakaoPublicKeys.getMatchesKey(headers.get(HEADER_KEY_ID));
        return getRSAPublicKey(kakaoPublicKey);
    }

    private PublicKey getRSAPublicKey(KakaoPublicKey publicKey) {
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