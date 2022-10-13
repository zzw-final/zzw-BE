package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;

import java.util.List;

@Getter
public class InfinitePostResponseDto {
    private Boolean isLast;
    private List<PostResponseDto> postList;

    public InfinitePostResponseDto(List<PostResponseDto> postResponseDtos, Boolean isLast) {
        this.postList = postResponseDtos;
        this.isLast = isLast;
    }
}
