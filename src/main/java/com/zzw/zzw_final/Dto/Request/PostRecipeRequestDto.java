package com.zzw.zzw_final.Dto.Request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PostRecipeRequestDto {
    private String title;
    private String foodName;
    private String time;
    private String imageUrl;
    private List<String> ingredient;
    private List<PostRecipeDetailRequestDto> pageList;

    public PostRecipeRequestDto(List<String> ingredient, String imageUrl,List<PostRecipeDetailRequestDto> recipeDetailRequestDtos){
        this.ingredient=ingredient;
        this.imageUrl = imageUrl;
        this.title = "제목";
        this.foodName = "닭볶음탕";
        this.time = "15분";
        this.pageList = recipeDetailRequestDtos;


    }
}

