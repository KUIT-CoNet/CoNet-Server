package com.kuit.conet.service;

import com.kuit.conet.common.exception.HistoryException;
import com.kuit.conet.dao.HistoryDao;
import com.kuit.conet.dao.PlanDao;
import com.kuit.conet.domain.storage.StorageDomain;
import com.kuit.conet.dto.request.history.HistoryRegisterRequest;
import com.kuit.conet.dto.response.history.HistoryRegisterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryDao historyDao;
    private final PlanDao planDao;
    private final StorageService storageService;

    public HistoryRegisterResponse registerToHistory(HistoryRegisterRequest registerRequest, MultipartFile historyImg) {
        Long planId = registerRequest.getPlanId();

        // 확정된 약속인지 검사
        if (!planDao.isFixedPlan(planId)) {
            throw new HistoryException(NOT_FIXED_PLAN);
        }

        // '오늘'을 기준으로 지난 약속인지 검사
        if (!planDao.isPastPlan(planId)) {
            throw new HistoryException(NOT_PAST_PLAN);
        }

        // history 에 이미 등록된 약속인지 확인
        if (planDao.isRegisteredToHistory(planId)) {
            throw new HistoryException(EXIST_HISTORY);
        }

        // 이미지의 경우 파일이 들어오지 않았을 때, S3 관련 행위를 일체 하지 않아야 함
        String imgUrl = null;
        if (!historyImg.isEmpty()){
            // 저장할 파일명 만들기
            // 받은 파일이 이미지 타입이 아닌 경우에 대한 유효성 검사 진행
            String fileName = storageService.getFileName(historyImg, StorageDomain.HISTORY, planId);
            // 새로운 이미지 S3에 업로드
            imgUrl = storageService.uploadToS3(historyImg, fileName);
        } else {
            log.warn("히스토리 이미지 파일이 입력되지 않았습니다.");
        }

        // history 에 등록
        return historyDao.registerToHistory(registerRequest, imgUrl);
    }
}