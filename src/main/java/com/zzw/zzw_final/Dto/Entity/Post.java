package com.zzw.zzw_final.Dto.Entity;

import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Post extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String title;

    @Column
    private String useremail;

    @Column
    private String thumbnail;

    @Column
    private int likeNum;

    @Column
    private String time;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TagList> tagLists;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes;

    public Post(PostRecipeRequestDto requestDto, Member member){
        this.title = requestDto.getTitle();
        this.time = requestDto.getTime();
        if(requestDto.getImageUrl().length() == 0){
            this.thumbnail = "https://zzwimage.s3.ap-northeast-2.amazonaws.com/Frame+3.png";
        }else{
            this.thumbnail = requestDto.getImageUrl();
        }
        this.likeNum = 0;
        this.member = member;
        this.useremail = member.getEmail();
    }

    public void update(PostRecipeRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.time = requestDto.getTime();
        if(requestDto.getImageUrl().length() == 0){
            this.thumbnail = "https://zzwimage.s3.ap-northeast-2.amazonaws.com/Frame+3.png";
        }else{
            this.thumbnail = requestDto.getImageUrl();
        }
    }

    public void updateLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }
}
