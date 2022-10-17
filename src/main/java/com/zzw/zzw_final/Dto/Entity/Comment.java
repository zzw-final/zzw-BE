package com.zzw.zzw_final.Dto.Entity;

import com.zzw.zzw_final.Dto.Request.CommentRequestDto;
import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Comment extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String comment;

    @Column
    private String useremail;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public Comment(CommentRequestDto requestDto, Post post, Member member){
        this.comment = requestDto.getComment();
        this.post = post;
        this.member = member;
        this.useremail = member.getEmail();
    }

    public void update(CommentRequestDto requestDto) {
        this.comment = requestDto.getComment();

    }
}
