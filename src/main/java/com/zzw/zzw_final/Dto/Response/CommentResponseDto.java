package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Comment;
import com.zzw.zzw_final.Dto.Entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {
    private Long userId;
    private String nickname;
    private String comment;
    private String grade;
    private String createdAt;

    public CommentResponseDto(Comment comment){
        this.userId = comment.getMember().getId();
        this.comment = comment.getComment();
        this.nickname = comment.getMember().getNickname();
        this.grade = comment.getMember().getGrade();
        this.createdAt = comment.getCreatedAt().toString();
    }
}
