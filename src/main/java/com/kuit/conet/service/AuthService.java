package com.kuit.conet.service;

import com.kuit.conet.auth.JwtTokenProvider;
import com.kuit.conet.auth.apple.AppleOAuthUserProvider;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse appleOAuthLogin(AppleLoginRequest loginRequest) {
        ApplePlatformUserResponse applePlatformUser = appleOAuthUserProvider.getApplePlatformUser(loginRequest.getIdToken());
        return generateLoginResponse(Platform.APPLE, applePlatformUser.getEmail(), applePlatformUser.getPlatformId());
    }

    private LoginResponse generateLoginResponse(Platform platform, String email, String platformId) {
        return userDao.findByPlatformAndPlatformId(platform, platformId)
                .map(userId -> {
                    User findUser = userDao.findById(userId).orElseThrow(NotFoundUserException::new);
                    return getLoginResponse(findUser);
                })
                .orElseGet(() -> {
                    User oauthUser = new User(email, platform, platformId);
                    User saveduser = userDao.save(oauthUser).get();
                    return getLoginResponse(saveduser);
                });
    }

    private LoginResponse getLoginResponse(User targetUser) {
        String accessToken = jwtTokenProvider.createAccessToken(targetUser.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(targetUser.getUserId());
        //TODO: Redis 에 refresh token 저장

        return new LoginResponse(accessToken, refreshToken, targetUser.getEmail());
    }

    public LoginResponse regenerateToken(RefreshTokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        // TODO: Redis 에서 해당 refresh token 찾기
        // TODO: 찾은 값의 validation 처리

        Long userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
        User existingUser = userDao.findById(userId).get();
        String newAccessToken = jwtTokenProvider.createAccessToken(existingUser.getUserId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(existingUser.getUserId());

        // TODO: Redis 에 재발급 받은 refresh token 저장

        return new LoginResponse(newAccessToken, newRefreshToken, existingUser.getEmail());
    }
}