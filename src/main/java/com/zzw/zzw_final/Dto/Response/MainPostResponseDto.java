package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MainPostResponseDto {
    private List<BestTagResponseDto> tagList;
    private List<PostResponseDto> bestPost;
    private List<PostResponseDto> recentPost;

    public MainPostResponseDto(List<BestTagResponseDto> tagList, List<PostResponseDto> bestPost,
                               List<PostResponseDto> recentPost){
        this.tagList = tagList;
        this.bestPost = bestPost;
        this.recentPost = recentPost;
    }
}
