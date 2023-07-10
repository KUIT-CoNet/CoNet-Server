package com.kuit.conet.utils.auth;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final long validityInMilliseconds;
    private final JwtParser jwtParser;

    private static final long ACCESS_TOKEN_EXPIRED_IN = 24 * 60 * 60 * 1000; // 24시간
    private static final long REFRESH_TOKEN_EXPIRED_IN = 15L * 24 * 60 * 60 * 1000; // 15일

    public JwtTokenProvider(@Value("${secret.jwt-secret-key}") String secretKey,
                            @Value("${secret.jwt-expired-in}") long validityInMilliseconds) {
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_TOKEN_EXPIRED_IN);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_EXPIRED_IN);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}