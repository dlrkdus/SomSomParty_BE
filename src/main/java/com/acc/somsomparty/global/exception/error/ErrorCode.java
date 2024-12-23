package com.acc.somsomparty.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    // Festival
    FESTIVAL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 축제입니다."),
    //Chatting
    FAILED_MESSAGE_SAVE(HttpStatus.BAD_REQUEST,"메세지 저장에 실패했습니다."),
    FAILED_MESSAGE_GET(HttpStatus.BAD_REQUEST, "메세지 조회에 실패했습니다.");

    private final HttpStatus httpStatus;    // HttpStatus
    private final String message;       // 설명
}
