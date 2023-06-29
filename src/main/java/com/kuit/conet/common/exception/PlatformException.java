package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_PLATFORM;

public class PlatformException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public PlatformException() {
        super(INVALID_PLATFORM.getMessage());
        this.exceptionStatus = INVALID_PLATFORM;
    }
}