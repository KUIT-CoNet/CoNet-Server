package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class HistoryException extends RuntimeException {
    private final ResponseStatus exceptionStatus;

    public HistoryException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public HistoryException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}