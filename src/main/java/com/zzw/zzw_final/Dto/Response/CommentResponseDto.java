package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Comment;
import com.zzw.zzw_final.Dto.Entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {
    private Long userId;
    private String profile;
    private Long commentId;
    private String nickname;
    private String comment;
    private String grade;
    private String createdAt;

    public CommentResponseDto(Comment comment){
        this.userId = comment.getMember().getId();
        this.profile = comment.getMember().getProfile();
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.nickname = comment.getMember().getNickname();
        this.grade = comment.getMember().getGrade();
        this.createdAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(comment.getModifiedAt());
    }
}
