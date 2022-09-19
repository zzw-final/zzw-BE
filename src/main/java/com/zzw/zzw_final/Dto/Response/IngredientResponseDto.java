package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.TagList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IngredientResponseDto {
    private String ingredientName;
    private Boolean isName;

    public IngredientResponseDto(TagList tagList){
        this.ingredientName = tagList.getName();
        this.isName = tagList.getIsTitle();
    }
}
