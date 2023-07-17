package com.kuit.conet.common.exception;

import com.kuit.conet.common.response.status.ResponseStatus;
import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {

    private final ResponseStatus exceptionStatus;

    public StorageException(ResponseStatus exceptionStatus) {
        super(exceptionStatus.getMessage());
        this.exceptionStatus = exceptionStatus;
    }

    public StorageException(ResponseStatus exceptionStatus, String message) {
        super(message);
        this.exceptionStatus = exceptionStatus;
    }
}