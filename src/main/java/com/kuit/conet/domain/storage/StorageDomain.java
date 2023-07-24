package com.kuit.conet.domain.storage;

import com.kuit.conet.common.exception.StorageException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

import static com.kuit.conet.common.response.status.BaseExceptionResponseStatus.INVALID_STORAGE_DOMAIN;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum StorageDomain {
    TEAM("team"),
    USER("user");

    private String storage;

    public static StorageDomain from(String storage) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(it.storage, storage))
                .findFirst()
                .orElseThrow(() -> new StorageException(INVALID_STORAGE_DOMAIN));
    }
}