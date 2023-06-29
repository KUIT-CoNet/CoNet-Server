package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.NOT_FOUND_USER;

public class NotFoundUserException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public NotFoundUserException() {
        super(NOT_FOUND_USER.getMessage());
        this.exceptionStatus = NOT_FOUND_USER;
    }
}