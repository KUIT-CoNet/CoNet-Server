package com.kuit.conet.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class BearerAuthInterceptor implements HandlerInterceptor {
    private AuthorizationExtractor authExtractor;
    //private JwtTokenProvider jwtTokenProvider;
    private JwtParser jwtParser;

    public BearerAuthInterceptor(AuthorizationExtractor authExtractor, JwtParser jwtParser) {
        this.authExtractor = authExtractor;
        this.jwtParser = jwtParser;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpRequest,
                             HttpServletResponse httpResponse, Object handler) {
        log.info("interceptor.preHandle 호출");
        String token = authExtractor.extract(httpRequest, "Bearer");
        httpRequest.setAttribute("token", token);
        log.info("Token: {}", token);

        if (token == null || token.length() == 0) {
            return true;
        }

        String userId = jwtParser.getUserIdFromToken(token);
        log.info("userId: {}", userId);
        httpRequest.setAttribute("userId", userId);
        return true;
    }
}