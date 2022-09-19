package com.zzw.zzw_final.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    //로그인(토큰) 관련 오류
    NULL_TOKEN("NULL_TOKEN", "로그인이 필요합니다."),
    INVALID_TOKEN("INVALID_TOKEN", "토큰의 유효기간이 지났습니다."),
    TOKEN_NOT_FOUND("TOKEN_NOT_FOUND", "존재하지 않는 Token 입니다.");

    private final String code;
    private final String message;
}

