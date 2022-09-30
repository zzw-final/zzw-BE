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
    private String imageUrl; // 대표이미지
    private List<String> ingredient;
    private List<PostRecipeDetailRequestDto> pageList;
}
