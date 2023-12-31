package com.kuit.conet.service;

import com.kuit.conet.dto.request.auth.OptionTermRequest;
import com.kuit.conet.utils.JwtParser;
import com.kuit.conet.utils.auth.JwtTokenProvider;
import com.kuit.conet.auth.apple.AppleUserProvider;
import com.kuit.conet.auth.kakao.KakaoUserProvider;
import com.kuit.conet.common.exception.InvalidTokenException;
import com.kuit.conet.common.exception.UserException;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.domain.auth.Platform;
import com.kuit.conet.domain.user.User;
import com.kuit.conet.dto.request.auth.LoginRequest;
import com.kuit.conet.dto.request.auth.PutOptionTermAndNameRequest;
import com.kuit.conet.dto.response.auth.AgreeTermAndPutNameResponse;
import com.kuit.conet.dto.response.auth.ApplePlatformUserResponse;
import com.kuit.conet.dto.response.auth.KakaoPlatformUserResponse;
import com.kuit.conet.dto.response.auth.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final AppleUserProvider appleUserProvider;
    private final KakaoUserProvider kakaoUserProvider;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtParser jwtParser;

    public LoginResponse appleLogin(LoginRequest loginRequest, String clientIp) {
        ApplePlatformUserResponse applePlatformUser = appleUserProvider.getApplePlatformUser(loginRequest.getIdToken());
        return generateLoginResponse(Platform.APPLE, applePlatformUser.getEmail(), applePlatformUser.getPlatformId(), clientIp);
    }

    public LoginResponse kakaoLogin(LoginRequest loginRequest, String clientIp) {
        KakaoPlatformUserResponse kakaoPlatformUser = kakaoUserProvider.getPayloadFromIdToken(loginRequest.getIdToken());
        return generateLoginResponse(Platform.KAKAO, kakaoPlatformUser.getEmail(), kakaoPlatformUser.getPlatformId(), clientIp);
    }

    private LoginResponse generateLoginResponse(Platform platform, String email, String platformId, String clientIp) {
        List<Long> findUserId = userDao.findByPlatformAndPlatformId(platform, platformId);
        if (!findUserId.isEmpty()) {
            User findUser = userDao.findById(findUserId.get(0));
            if (findUser == null) {
                throw new UserException(NOT_FOUND_USER);
            }

            // 회원가입은 되어있는데, 필수 약관 동의 혹은 이름 입력이 되어있지 않은 유저
            if (!findUser.getServiceTerm() || findUser.getName() == null) {
                log.info("회원가입은 되어 있으나, 약관 동의 및 이름 입력이 필요합니다.");
                return getLoginResponse(findUser, clientIp, false);
            } else {
                // 이미 회원가입과 약관 동의 및 이름 입력이 모두 되어있는 유저
                log.info("로그인에 성공하였습니다.");
                return getLoginResponse(findUser, clientIp, true);
            }
        } else {
            // 회원가입이 필요한 멤버
            User oauthUser = new User(email, platform, platformId);
            User savedUser = userDao.save(oauthUser);
            log.info("회원가입 성공! 약관 동의 및 이름 입력이 필요합니다.");
            return getLoginResponse(savedUser, clientIp, false);
        }
    }

    private LoginResponse getLoginResponse(User targetUser, String clientIp, Boolean isRegistered) {
        String accessToken = jwtTokenProvider.createAccessToken(targetUser.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(targetUser.getUserId());
        // Redis 에 refresh token 저장
        redisTemplate.opsForValue().set(refreshToken, clientIp);

        return new LoginResponse(targetUser.getEmail(), accessToken, refreshToken, isRegistered);
    }

    public LoginResponse regenerateToken(String refreshToken,  String clientIp) {
        // Redis 에서 해당 refresh token 찾기
        String existingIp = redisTemplate.opsForValue().get(refreshToken);

        // 찾은 값의 validation 처리
        if (existingIp == null) {
            throw new InvalidTokenException(INVALID_REFRESH_TOKEN);
        } else if (!existingIp.equals(clientIp)) {
            throw new InvalidTokenException(IP_MISMATCH);
        }

        Long userId = Long.parseLong(jwtParser.getUserIdFromToken(refreshToken));
        User existingUser = userDao.findById(userId);
        return getLoginResponse(existingUser, clientIp, true);
    }

    public AgreeTermAndPutNameResponse agreeTermAndPutName(PutOptionTermAndNameRequest nameRequest, HttpServletRequest httpRequest, String clientIp) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        // 이용 약관 및 이름 입력 DB update
        User user = userDao.agreeTermAndPutName(nameRequest.getName(), nameRequest.getOptionTerm(), userId);

        return new AgreeTermAndPutNameResponse(user.getName(), user.getEmail(), user.getServiceTerm(), user.getOptionTerm());
    }

    public void updateOptionTerm(OptionTermRequest optionTermRequest, HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));

        // 선택 약관에 대한 데이터베이스 값과 입력 값 비교
        // -> 동일하면 exception
        if (optionTermRequest.getOption() == userDao.getOptionTerm(userId)) {
            throw new UserException(OPTION_TERM_ALREADY_SET);
        }

        userDao.updateOptionTerm(optionTermRequest, userId);
    }
}