package com.zzw.zzw_final.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    NULL_TOKEN("NULL_TOKEN", "로그인이 필요합니다."),
    INVALID_TOKEN("INVALID_TOKEN", "토큰의 유효기간이 지났습니다."),
    NULL_OAUTH("NULL_OAUTH", "헤더에 oauth 값이 없음"),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "해당하는 유저가 없습니다."),
    TOKEN_NOT_FOUND("TOKEN_NOT_FOUND", "존재하지 않는 Token 입니다."),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME","중복된 닉네임입니다."),
    NOT_EQUAL_MEMBER("NOT_EQUAL_MEMBER","작성하신 글이 아닙니다."),
    NULL_FILE("NULL_FILE", "multipartFile이 null값입니다."),
    DUPLICATE_POST("DUPLICATE_POST","작성하신 글이 이미 등록되었습니다."),
    NOTFOUND_POST_ID("NOTFOUND_POST_ID", "해당하는 레시피 id가 없음"),
    NOTFOUND_POST("NOTFOUND_POST", "좋아요한 레시피가 없음"),
    SAME_PERSON("SAME_PERSON","같은 사용자입니다."),
    NOTFOUND_ROOM("NOTFOUND_ROOM", "Room id가 잘못되었습니다."),
    INVALID_MEMBER("INVALID_MEMBER", "해당 유저는 권한이 없습니다."),
    IS_NOT_HANGEUL("IS_NOT_HANGEUL", "한글이 아닙니다.");

    private final String code;
    private final String message;
}

