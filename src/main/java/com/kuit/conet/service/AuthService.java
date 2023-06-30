package com.kuit.conet.service;

import com.kuit.conet.auth.JwtTokenProvider;
import com.kuit.conet.auth.apple.AppleOAuthUserProvider;
import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.common.exception.NotFoundUserException;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.domain.Platform;
import com.kuit.conet.domain.User;
import com.kuit.conet.dto.request.AppleLoginRequest;
import com.kuit.conet.dto.request.RefreshTokenRequest;
import com.kuit.conet.dto.response.ApplePlatformUserResponse;
import com.kuit.conet.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.module.InvalidModuleDescriptorException;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_REFRESHTOKEN;
import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.IP_MISMATCH;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public LoginResponse appleOAuthLogin(AppleLoginRequest loginRequest, String clientIp) {
        ApplePlatformUserResponse applePlatformUser = appleOAuthUserProvider.getApplePlatformUser(loginRequest.getIdToken());
        return generateLoginResponse(Platform.APPLE, applePlatformUser.getEmail(), applePlatformUser.getPlatformId(), clientIp);
    }

    private LoginResponse generateLoginResponse(Platform platform, String email, String platformId, String clientIp) {
        return userDao.findByPlatformAndPlatformId(platform, platformId)
                .map(userId -> {
                    User findUser = userDao.findById(userId).orElseThrow(NotFoundUserException::new);
                    return getLoginResponse(findUser, clientIp);
                })
                .orElseGet(() -> {
                    User oauthUser = new User(email, platform, platformId);
                    User saveduser = userDao.save(oauthUser).get();
                    return getLoginResponse(saveduser, clientIp);
                });
    }

    private LoginResponse getLoginResponse(User targetUser, String clientIp) {
        String accessToken = jwtTokenProvider.createAccessToken(targetUser.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(targetUser.getUserId());
        // Redis 에 refresh token 저장
        redisTemplate.opsForValue().set(refreshToken, clientIp);

        return new LoginResponse(accessToken, refreshToken, targetUser.getEmail());
    }

    public LoginResponse regenerateToken(RefreshTokenRequest tokenRequest, String clientIp) {
        String refreshToken = tokenRequest.getRefreshToken();
        // Redis 에서 해당 refresh token 찾기
        String existingIp = redisTemplate.opsForValue().get(refreshToken);
        // 찾은 값의 validation 처리
        if (existingIp == null) {
            throw new InvalidTokenException(INVALID_REFRESHTOKEN);
        } else if (!existingIp.equals(clientIp)) {
            throw new InvalidTokenException(IP_MISMATCH);
        }

        Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
        User existingUser = userDao.findById(userId).get();
        String newAccessToken = jwtTokenProvider.createAccessToken(existingUser.getUserId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(existingUser.getUserId());

        // Redis 에 재발급 받은 refresh token 저장
        redisTemplate.opsForValue().set(newRefreshToken, clientIp);

        return new LoginResponse(newAccessToken, newRefreshToken, existingUser.getEmail());
    }
}