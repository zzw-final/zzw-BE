package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Content;
import lombok.Getter;

@Getter
public class ContentResponseDto {
    private String imageUrl;
    private String content;

    public ContentResponseDto(Content content){
        this.imageUrl = content.getImage();
        this.content = content.getContent();
    }
}
