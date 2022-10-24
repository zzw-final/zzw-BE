package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;

@Getter
public class FollowPostResponseDto {
    private Boolean isGet;
    private Boolean isFollow;

    public FollowPostResponseDto(Boolean isGet, Boolean isFollow){
        this.isGet = isGet;
        this.isFollow = isFollow;
    }
}
