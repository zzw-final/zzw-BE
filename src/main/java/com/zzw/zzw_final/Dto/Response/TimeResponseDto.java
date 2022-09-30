package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Content;
import com.zzw.zzw_final.Dto.Entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class TimeResponseDto {
    private Long postId;
    private String title;
    private String nickname;
    private int likeNum;
    private Boolean isLike;
    private String time;
    private List<IngredientResponseDto> ingredient;
    private String createAt;
    private String foodImg;


    public TimeResponseDto(Post post, List<IngredientResponseDto> responseDtos, Boolean isLike){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.isLike = isLike;
        this.createAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(post.getModifiedAt());
        this.nickname = post.getMember().getNickname();
        this.likeNum = post.getLikeNum();
        this.foodImg = post.getThumbnail();
        this.ingredient = responseDtos;
        this.time = post.getTime();
    }
}