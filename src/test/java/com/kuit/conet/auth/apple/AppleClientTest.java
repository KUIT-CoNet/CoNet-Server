/*
package com.kuit.conet.auth.apple;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class AppleClientTest {
    @Autowired
    private AppleClient appleClient;

    @Test
    @DisplayName("Apple 서버와 통신하여 public key 받기")
    void getPublicKeys() {
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        List<ApplePublicKey> keys = applePublicKeys.getKeys();

        boolean isRequestedKeysNonNull = keys.stream().allMatch(this::isAllNotNull);
        assertThat(isRequestedKeysNonNull).isTrue();

        log.info("kid1: {}", keys.get(0).getKid());
        log.info("alg1: {}", keys.get(0).getAlg());
        log.info("kid2: {}", keys.get(1).getKid());
        log.info("alg2: {}", keys.get(1).getAlg());
    }

    private boolean isAllNotNull(ApplePublicKey applePublicKey) {
        return Objects.nonNull(applePublicKey.getKty()) && Objects.nonNull(applePublicKey.getKid()) &&
                Objects.nonNull(applePublicKey.getUse()) && Objects.nonNull(applePublicKey.getAlg()) &&
                Objects.nonNull(applePublicKey.getN()) && Objects.nonNull(applePublicKey.getE());
    }
}*/
