package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetGradeResponseDto {
    private Boolean isGet;

    public GetGradeResponseDto(Boolean isGet){
        this.isGet = isGet;
    }
}
