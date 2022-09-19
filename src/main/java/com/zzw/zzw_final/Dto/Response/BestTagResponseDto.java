package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Tag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BestTagResponseDto {
    private String tagName;

    public BestTagResponseDto(Tag tag){
        this.tagName = tag.getName();
    }
}
