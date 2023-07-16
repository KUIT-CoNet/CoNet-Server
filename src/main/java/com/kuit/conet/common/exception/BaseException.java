package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public BaseException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}