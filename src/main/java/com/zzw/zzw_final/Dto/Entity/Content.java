package com.zzw.zzw_final.Dto.Entity;

import com.zzw.zzw_final.Dto.Request.PostRecipeDetailRequestDto;
import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String image;

    @Column
    private String content;

    @Column
    private int page;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public Content(String url, String content, Post post){
        this.image = url;
        this.content = content;
        this.post = post;
    }

    public Content(PostRecipeDetailRequestDto postRecipeDetailRequestDto, Post post) {
        this.image = postRecipeDetailRequestDto.getImageUrl();
        this.page = postRecipeDetailRequestDto.getPage();
        this.content = postRecipeDetailRequestDto.getContent();
        this.post = post;
    }

    public void update(PostRecipeRequestDto requestDto) {
        //this.content= requestDto.getContent();
        this.image = requestDto.getImageUrl();
    }

    public void update(PostRecipeDetailRequestDto postRecipeDetailRequestDto) {
        this.image = postRecipeDetailRequestDto.getImageUrl();
        this.content = postRecipeDetailRequestDto.getContent();
        this.page = postRecipeDetailRequestDto.getPage();
    }
}
