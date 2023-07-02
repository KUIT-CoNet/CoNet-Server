package com.kuit.conet.common.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus{
    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(1000, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /**
     * 2000: Request 오류 (BAD_REQUEST)
     */
    BAD_REQUEST(2000, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 요청입니다."),
    URL_NOT_FOUND(2001, HttpStatus.BAD_REQUEST.value(), "유효하지 않은 URL 입니다."),
    METHOD_NOT_ALLOWED(2002, HttpStatus.METHOD_NOT_ALLOWED.value(), "해당 URL에서는 지원하지 않는 HTTP Method 입니다."),

    /**
     * 3000: Server, Database 오류 (INTERNAL_SERVER_ERROR)
     */
    SERVER_ERROR(3000, HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에서 오류가 발생하였습니다."),
    DATABASE_ERROR(3001, HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스에서 오류가 발생하였습니다."),
    BAD_SQL_GRAMMAR(3002, HttpStatus.INTERNAL_SERVER_ERROR.value(), "SQL에 오류가 있습니다."),

    /**
     * 4000: Authorization 오류
     */
    JWT_ERROR(4000, HttpStatus.UNAUTHORIZED.value(), "JWT에서 오류가 발생하였습니다."),
    TOKEN_NOT_FOUND(4001, HttpStatus.BAD_REQUEST.value(), "토큰이 HTTP Header에 없습니다."),
    UNSUPPORTED_TOKEN_TYPE(4002, HttpStatus.BAD_REQUEST.value(), "지원되지 않는 토큰 형식입니다."),
    UNSUPPORTED_TOKEN_TYPE_FOR_APPLE(4003, HttpStatus.BAD_REQUEST.value(), "Apple OAuth Identity Token의 형식이 올바르지 않습니다."),
    UNSUPPORTED_TOKEN_TYPE_FOR_KAKAO(4004, HttpStatus.BAD_REQUEST.value(), "Kakao OAuth Identity Token의 형식이 올바르지 않습니다."),
    INVALID_TOKEN(4007, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),
    MALFORMED_TOKEN(4008, HttpStatus.UNAUTHORIZED.value(), "올바르지 않은 토큰입니다."),
    EXPIRED_TOKEN(4009, HttpStatus.UNAUTHORIZED.value(), "로그인 인증 유효기간이 만료되었습니다."),
    TOKEN_MISMATCH(4010, HttpStatus.UNAUTHORIZED.value(), "로그인 정보가 토큰 정보와 일치하지 않습니다."),
    INVALID_CLAIMS(4011, HttpStatus.UNAUTHORIZED.value(), "Apple OAuth Claims 값이 올바르지 않습니다."),
    INVALID_REFRESHTOKEN(4012, HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 Refresh Token입니다."),
    IP_MISMATCH(4013, HttpStatus.UNAUTHORIZED.value(), "다른 IP에서 접속했습니다. 다시 로그인해주세요."),

    /**
     * 5000: 회원 정보 오류
     * */
    INVALID_PLATFORM(5001, HttpStatus.BAD_REQUEST.value(),"플랫폼 정보가 올바르지 않습니다."),
    NOT_FOUND_USER(5002, HttpStatus.BAD_REQUEST.value(),"사용자를 찾을 수 없습니다.");

    private final int code;
    private final int status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
