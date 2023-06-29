package com.kuit.conet.security.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kuit.conet.common.exception.InvalidTokenException;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import java.util.Base64;

import java.util.Map;

@Component
public class AppleJwtParser {
    private static final String IDENTITY_TOKEN_SPLITER = "\\.";
    private static final int HEADER_INDEX = 0;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> parseHeaders(String identityToken) {
        try {
            String encodeHeader = identityToken.split(IDENTITY_TOKEN_SPLITER)[HEADER_INDEX];
            String decodeHeader = new String(Base64.getDecoder().decode(encodeHeader));
            return objectMapper.readValue(decodeHeader, Map.class);
        } catch (JsonProcessingException | ArrayIndexOutOfBoundsException e) {
            throw new InvalidTokenException();
        }
    }
}
