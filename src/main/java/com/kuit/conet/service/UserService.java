package com.kuit.conet.service;

import com.kuit.conet.utils.JwtParser;
import com.kuit.conet.dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final JwtParser jwtParser;

    public void userDelete(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        userDao.deleteUser(userId);
    }
}