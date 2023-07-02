package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;

public class InvalidTokenException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public InvalidTokenException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.exceptionStatus = responseStatus;
    }
}
