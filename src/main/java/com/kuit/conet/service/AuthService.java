package com.kuit.conet.service;

import com.kuit.conet.auth.JwtTokenProvider;
import com.kuit.conet.auth.apple.AppleOAuthUserProvider;
import com.kuit.conet.common.exception.NotFoundUserException;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.domain.Platform;
import com.kuit.conet.domain.User;
import com.kuit.conet.dto.request.AppleLoginRequest;
import com.kuit.conet.dto.response.ApplePlatformUserResponse;
import com.kuit.conet.dto.response.OAuthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    // Todo: AuthService 회원 존재 여부에 따라 다른 Response 형식 반환 구현
    private final UserDao userDao;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final JwtTokenProvider jwtTokenProvider;

    public OAuthTokenResponse appleOAuthLogin(AppleLoginRequest loginRequest) {
        ApplePlatformUserResponse applePlatformUser = appleOAuthUserProvider.getApplePlatformUser(loginRequest.getToken());
        return generateOAuthTokenResponse(Platform.APPLE, applePlatformUser.getEmail(), applePlatformUser.getPlatformId());
    }

    private OAuthTokenResponse generateOAuthTokenResponse(Platform platform, String email, String platformId) {
        return userDao.findByPlatformAndPlatformId(platform, platformId)
                .map(userId -> {
                    User findUser = userDao.findById(userId).orElseThrow(NotFoundUserException::new);
                    String token = issueToken(findUser);
                    return new OAuthTokenResponse(token, findUser.getEmail(), true, platformId);
                })
                .orElseGet(() -> {
                    User oauthUser = new User(email, platform, platformId);
                    User saveduser = userDao.save(oauthUser);
                    String token = issueToken(saveduser);
                    return new OAuthTokenResponse(token, email, false, platformId);
                });
    }
    private String issueToken(final User findUser) {
        return jwtTokenProvider.createToken(findUser.getUserId());
    }
}