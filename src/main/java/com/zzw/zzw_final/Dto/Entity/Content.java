package com.zzw.zzw_final.Dto.Entity;

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

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public Content(String url, String content, Post post){
        this.image = url;
        this.content = content;
        this.post = post;
    }

    public void update(PostRecipeRequestDto requestDto) {
        this.content= requestDto.getContent();
        this.image = requestDto.getImageUrl();
    }
}
