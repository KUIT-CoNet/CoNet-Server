package com.kuit.conet.service;

import com.kuit.conet.utils.JwtParser;
import com.kuit.conet.dao.UserDao;
import com.kuit.conet.dto.request.TokenRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final JwtParser jwtParser;

    public void userDelete(TokenRequest tokenRequest) {
        userDao.deleteUser(Long.parseLong(jwtParser.getUserIdFromToken(tokenRequest.getToken())));
    }
}