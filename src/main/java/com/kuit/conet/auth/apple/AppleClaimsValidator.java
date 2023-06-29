package com.kuit.conet.auth.apple;

import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class AppleClaimsValidator {
    // 클라이언트로부터 전달 받은 Apple 의 id_token 이 nonce 필드를 포함하지 않음
    //private static final String NONCE_KEY = "nonce";

    private final String iss;
    private final String clientId;
    //private final String nonce;

    public AppleClaimsValidator (
            @Value("${oauth.apple.iss}") String iss,
            @Value("${oauth.apple.client-id}") String clientId
            //@Value("${oauth.apple.nonce}") String nonce
    ) {
        this.iss = iss;
        this.clientId = clientId;
        //this.nonce = EncryptUtils.encrypt(nonce);
    }

    public boolean isValid(Claims claims) {
        return claims.getIssuer().contains(iss) &&
                claims.getAudience().equals(clientId); // &&
                //claims.get(NONCE_KEY, String.class).equals(nonce);
    }
}

/**
 * iss, client-id, nonce validation
 *
 * client-id: Apple developers Application Bundle Id
 * nonce: 임의의 값
 * */