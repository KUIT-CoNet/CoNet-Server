package com.kuit.conet.service;

import com.kuit.conet.common.exception.UserException;
import com.kuit.conet.dto.request.user.ImgRequest;
import com.kuit.conet.dto.request.user.NameRequest;
import com.kuit.conet.dto.response.user.UserResponse;
import com.kuit.conet.dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public void userDelete(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        userDao.deleteUser(userId);
    }

    public UserResponse getUser(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        isExistUser(userId);

        return userDao.getUser(userId);
    }

    public void updateImg(HttpServletRequest httpRequest, ImgRequest imgRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        isExistUser(userId);

        userDao.updateImg(userId, imgRequest.getImgUrl());
    }

    public void updateName(HttpServletRequest httpRequest, NameRequest nameRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        isExistUser(userId);

        userDao.updateName(userId, nameRequest.getName());
    }

    private void isExistUser(Long userId) {
        // 존재하는 유저인지 검사
        if (!userDao.isExistUser(userId)) {
            throw new UserException(NOT_FOUND_USER);
        }
    }
}