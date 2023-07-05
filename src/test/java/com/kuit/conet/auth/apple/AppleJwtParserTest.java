/*
package com.kuit.conet.auth.apple;

import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.common.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.*;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class AppleJwtParserTest {
    private AppleJwtParser appleJwtParser = new AppleJwtParser();

    @Test
    @DisplayName("Apple id token 헤더 파싱")
    void parseHeader() throws NoSuchAlgorithmException {
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        String idToken = Jwts.builder()
                .setHeaderParam("kid", "WAHF2345")
                .claim("id", "123456789")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + 1000*60*60*24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        Map<String, String> actual = appleJwtParser.parseHeaders(idToken);
        assertThat(actual).containsKeys("alg", "kid");
        //log.info("alg: {}, kid: {}", actual.get("alg"), actual.get("kid"));
    }

    @Test
    @DisplayName("올바르지 않은 형식의 token으로 헤더 파싱")
    void parseHeadersWithInvalidToken() {
        assertThatThrownBy(() -> appleJwtParser.parseHeaders("invalidToken"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("id token과 public key로 사용자 정보가 포함된 Claims 반환")
    void parsePublicKeyToGetClaims() throws NoSuchAlgorithmException {
        String expected = "19281729"; // platformId
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String idToken = Jwts.builder()
                .setHeaderParam("kid", "WAHF2345")
                .claim("id", "123456789")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject(expected)
                .setExpiration(new Date(now.getTime() + 1000*60*60*24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(idToken, publicKey);

        assertThat(claims).isNotEmpty();
        assertThat(claims.getSubject()).isEqualTo(expected);
    }

    @Test
    @DisplayName("만료된 id token를 받을 경우, Claims 획득 시 예외 반환")
    void parseExpiredTokenClaims() throws NoSuchAlgorithmException {
        String expected = "19281729"; // platformId
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String idToken = Jwts.builder()
                .setHeaderParam("kid", "WAHF2345")
                .claim("id", "123456789")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject(expected)
                .setExpiration(new Date(now.getTime() - 1L))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        assertThatThrownBy(() -> appleJwtParser.parsePublicKeyAndGetClaims(idToken, publicKey))
                .isInstanceOf(TokenExpiredException.class);
    }

    @Test
    @DisplayName("올바르지 않는 public key로 Claims 획득 시 예외 반환")
    void invalidPublicKey() throws NoSuchAlgorithmException {
        Date now = new Date();
        String expected = "19281729"; // platformId
        PublicKey differentPublicKey = KeyPairGenerator.getInstance("RSA").generateKeyPair().getPublic();
        PrivateKey privateKey = KeyPairGenerator.getInstance("RSA").generateKeyPair().getPrivate();
        String idToken = Jwts.builder()
                .setHeaderParam("kid", "WAHF2345")
                .claim("id", "123456789")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject(expected)
                .setExpiration(new Date(now.getTime() - 1L))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        assertThatThrownBy(() -> appleJwtParser.parsePublicKeyAndGetClaims(idToken, differentPublicKey))
                .isInstanceOf(InvalidTokenException.class);
    }
}*/
