package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class TeamException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public TeamException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }
}
