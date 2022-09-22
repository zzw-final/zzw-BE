package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageUrlResponseDto {
    private String imageUrl;

    public ImageUrlResponseDto(String url){
        this.imageUrl = url;
    }
}
