package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Content;
import com.zzw.zzw_final.Dto.Entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private Long postId;
    private String title;
    private String nickname;
    private int likeNum;
    private List<IngredientResponseDto> ingredient;
    private String createAt;
    private String foodImg;

    public PostResponseDto(Post post, Content content, List<IngredientResponseDto> responseDtos){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.createAt = post.getCreatedAt().toString();
        this.nickname = post.getMember().getNickname();
        this.likeNum = post.getLikeNum();
        this.foodImg = content.getImage();
        this.ingredient = responseDtos;
    }
}
