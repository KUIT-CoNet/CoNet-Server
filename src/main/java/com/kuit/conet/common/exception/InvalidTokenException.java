package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public InvalidTokenException(ResponseStatus responseStatus) {
        super(responseStatus.getMessage());
        this.exceptionStatus = responseStatus;
    }

    public InvalidTokenException(ResponseStatus responseStatus, String message) {
        super(message);
        this.exceptionStatus = responseStatus;
    }
}
