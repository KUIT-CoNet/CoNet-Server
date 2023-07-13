/*
package com.kuit.conet.auth.apple;

import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.dto.response.auth.ApplePlatformUserResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.*;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class AppleOAuthUserProviderTest {
    @Autowired
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @MockBean
    private AppleClient appleClient;
    @MockBean
    private PublicKeyGenerator publicKeyGenerator;
    @MockBean
    private AppleClaimsValidator appleClaimsValidator;

    @Test
    @DisplayName("Apple OAuth 유저 접속 시 platform Id 반환")
    void getApplePlatformMember() throws NoSuchAlgorithmException {
        String expected = "19281729";
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        String identityToken = Jwts.builder()
                .setHeaderParam("kid", "WAHF2345")
                .claim("id", "12345678")
                .claim("email", "asdf@gmail.com")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject(expected)
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        //System.out.println(identityToken.toString());

        when(appleClient.getApplePublicKeys()).thenReturn(mock(ApplePublicKeys.class));
        when(publicKeyGenerator.generatePublicKey(any(), any())).thenReturn(publicKey);
        when(appleClaimsValidator.isValid(any())).thenReturn(true);

        ApplePlatformUserResponse actual = appleOAuthUserProvider.getApplePlatformUser(identityToken);

        assertAll(
                () -> assertThat(actual.getPlatformId()).isEqualTo(expected),
                () -> assertThat(actual.getEmail()).isEqualTo("asdf@gmail.com")
        );
    }

    @Test
    @DisplayName("Claim 검증에 실패할 경우 예외 반환")
    void invalidClaims() throws NoSuchAlgorithmException {
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        String identityToken = Jwts.builder()
                .setHeaderParam("kid", "WAHF2345")
                .claim("id", "12345678")
                .claim("email", "asdf@gmail.com")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject("19281729")
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        when(appleClient.getApplePublicKeys()).thenReturn(mock(ApplePublicKeys.class));
        when(publicKeyGenerator.generatePublicKey(any(), any())).thenReturn(publicKey);
        when(appleClaimsValidator.isValid(any())).thenReturn(false);

        assertThatThrownBy(() -> appleOAuthUserProvider.getApplePlatformUser(identityToken))
                .isInstanceOf(InvalidTokenException.class);
    }
}*/
