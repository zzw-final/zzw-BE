package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;

@Getter
public class CommentGradeResponseDto {
    private Boolean isGet;
    private CommentResponseDto comment;


    public CommentGradeResponseDto(boolean isGet, CommentResponseDto comment) {
        this.isGet = isGet;
        this.comment = comment;
    }
}
