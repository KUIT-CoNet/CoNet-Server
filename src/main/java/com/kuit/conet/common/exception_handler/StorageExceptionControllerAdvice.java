package com.kuit.conet.common.exception_handler;

import com.kuit.conet.common.exception.PlatformException;
import com.kuit.conet.common.exception.StorageException;
import com.kuit.conet.common.response.BaseErrorResponse;
import jakarta.annotation.Priority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Priority(0)
@RestControllerAdvice
public class StorageExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StorageException.class)
    public BaseErrorResponse handel_PlatformException(StorageException e) {
        log.error("[handle_StorageException]", e);
        return new BaseErrorResponse(e.getExceptionStatus(), e.getMessage());
    }
}