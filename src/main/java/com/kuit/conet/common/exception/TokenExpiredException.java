package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;

public class TokenExpiredException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public TokenExpiredException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.exceptionStatus = responseStatus;
    }
}