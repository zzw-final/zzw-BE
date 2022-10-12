package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;

import java.util.List;

@Getter
public class InfinitePostResponseDto {
    private List<PostResponseDto> postList;

    public InfinitePostResponseDto(List<PostResponseDto> postResponseDtos, int page) {
        this.postList = postResponseDtos;
    }
}
