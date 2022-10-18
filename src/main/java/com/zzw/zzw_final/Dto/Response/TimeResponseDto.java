package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Follow;
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
    private Long authorId;
    private String nickname;
    private String grade;
    private int likeNum;
    private String profile;
    private Boolean isLike;
    private Boolean isFollow;
    private String time;
    private List<IngredientResponseDto> ingredient;
    private String createAt;
    private String foodImg;

    private List<ContentResponseDto> contentList;


    public TimeResponseDto(Post post, List<IngredientResponseDto> responseDtos,
                           Boolean isLike, List<ContentResponseDto> contentList, Follow follow){
        this.postId = post.getId();
        this.title = post.getTitle();
        this.isLike = isLike;
        this.profile = post.getMember().getProfile();
        this.createAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(post.getCreatedAt());
        this.nickname = post.getMember().getNickname();
        this.authorId = post.getMember().getId();
        this.grade = post.getMember().getGrade();
        this.likeNum = post.getLikeNum();
        this.foodImg = post.getThumbnail();
        this.contentList = contentList;
        this.ingredient = responseDtos;
        this.time = post.getTime().substring(0, post.getTime().length()-1);
        this.isFollow = (follow==null) ? false : true;
    }
}