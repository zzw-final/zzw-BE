package com.zzw.zzw_final.Dto.Request;

import lombok.Getter;

@Getter
public class PostRecipeDetailRequestDto {
    private String imageUrl;
    private String content;
    private int page;

    public PostRecipeDetailRequestDto(){
        this.page = 1;
        this.content = "내용";
        this.imageUrl ="";
    }
}
