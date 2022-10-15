package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;

@Getter
public class PostRecipeResponseDto {
    private Long postId;
    private Boolean isGet;

    public PostRecipeResponseDto(Long postId, Boolean isGet){
        this.postId = postId;
        this.isGet = isGet;
    }
}
