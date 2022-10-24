package com.zzw.zzw_final.Dto.Entity;

import com.zzw.zzw_final.Dto.Request.PostRecipeDetailRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
