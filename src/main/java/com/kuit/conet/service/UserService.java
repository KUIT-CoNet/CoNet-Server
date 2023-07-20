package com.kuit.conet.service;

import com.kuit.conet.common.exception.UserException;
import com.kuit.conet.domain.StorageDomain;
import com.kuit.conet.dto.request.user.NameRequest;
import com.kuit.conet.dto.response.StorageImgResponse;
import com.kuit.conet.dto.response.user.UserResponse;
import com.kuit.conet.dao.UserDao;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;
    private final StorageService storageService;
    private final String  URL_SPLITER = "/";

    public void userDelete(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        userDao.deleteUser(userId);
    }

    public UserResponse getUser(HttpServletRequest httpRequest) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        isExistUser(userId);

        // S3에 없는 객체에 대한 유효성 검사
        String imgUrl = userDao.getUserImgUrl(userId);
        String fileName = imgUrl.split(URL_SPLITER)[3];
        if(!storageService.isExistImage(fileName)) {
            log.warn("S3 버킷에 존재하지 않는 이미지입니다. 기본 이미지로 변경하겠습니다.");
            userDao.setImageUrlDefault(userId);
        }

        return userDao.getUser(userId);
    }

    public StorageImgResponse updateImg(HttpServletRequest httpRequest, MultipartFile file) {
        Long userId = Long.parseLong((String) httpRequest.getAttribute("userId"));
        isExistUser(userId);

        // 유저의 프로필 이미지가 기본 프로필 이미지인지 확인 -> 기본 이미지가 아니면 기존 이미지를 S3에서 이미지 삭제
        if (!userDao.isDefaultImage(userId)) {
            String imgUrl = userDao.getUserImgUrl(userId);
            String fileName = imgUrl.split(URL_SPLITER)[3];
            log.info("delete fileName: {}", fileName);
            storageService.deleteImage(fileName);
        }

        // 새로운 이미지 S3에 업로드
        String imgUrl = storageService.uploadImage(file, StorageDomain.USER, userId);

        return userDao.updateImg(userId, imgUrl);
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