package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.UNSUPPORTED_TOKEN_TYPE_FOR_APPLE;

public class InvalidTokenException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public InvalidTokenException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.exceptionStatus = responseStatus;
    }
}
