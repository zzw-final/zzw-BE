package com.zzw.zzw_final.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    //로그인(토큰) 관련 오류
    NULL_TOKEN("NULL_TOKEN", "로그인이 필요합니다."),
    INVALID_TOKEN("INVALID_TOKEN", "토큰의 유효기간이 지났습니다."),

    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "해당하는 유저가 없습니다."),
    INVALID_MEMBER("INVALID_MEMBER", "비밀번호가 일치하지 않습니다."),
    TOKEN_NOT_FOUND("TOKEN_NOT_FOUND", "존재하지 않는 Token 입니다."),


    //회원가입 관련 오류
    DUPLICATE_EMAIL("DUPLICATE_EMAIL", "이미 이메일이 사용중입니다."),

    //게시글 관련 오류
    NOT_EQUAL_MEMBER("NOT_EQUAL_MEMBER","작성하신 글이 아닙니다."),

    //제품(Product) 관련 오류
    INVALID_PRODUCT("INVALID_PRODUCT", "해당하는 제품이 없습니다."),

    //장바구니 관련 오류
    INVALID_CART("INVALID_CART", "해당하는 제품이 장바구니에 없습니다."),
    DUPLICATE_CART("DUPLICATE_CART", "해당하는 제품이 장바구니에 이미 있습니다."),

    //소셜로그인 관련 오류
    NOTFOUND_LOGIN_TYPE("NOTFOUND_LOGIN_TYPE", "해당하는 소셜로그인 종류가 없습니다."),
    NOT_GOOGLE("NOT_GOOGLE","구글 로그인이 안됐음");


    private final String code;
    private final String message;
}

