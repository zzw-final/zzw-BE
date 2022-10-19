package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetGradeResponseDto {
    private Boolean isGet;
    private Boolean isLike;

    public GetGradeResponseDto(Boolean isGet, Boolean isLike){
        this.isGet = isGet;
        this.isLike = isLike;
    }
    public GetGradeResponseDto(Boolean isGet){
        this.isGet = isGet;
    }
}
