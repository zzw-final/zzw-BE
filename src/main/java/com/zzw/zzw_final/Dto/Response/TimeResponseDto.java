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
    private String time;
    private String content;
    private List<IngredientResponseDto> ingredient;
    private String createAt;
    private String foodImg;

    private List<CommentResponseDto> commentList;

    public TimeResponseDto(Post post, Content content, List<IngredientResponseDto> responseDtos, List<CommentResponseDto> commentList){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.createAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(post.getModifiedAt());
        this.nickname = post.getMember().getNickname();
        this.likeNum = post.getLikeNum();
        this.foodImg = content.getImage();
        this.ingredient = responseDtos;
        this.time = post.getTime();
        this.content = content.getContent();
        this.commentList = commentList;
    }
}