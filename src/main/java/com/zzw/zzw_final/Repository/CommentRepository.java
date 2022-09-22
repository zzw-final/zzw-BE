package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Comment;
import com.zzw.zzw_final.Dto.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findPostById(Long id);
    Comment findCommentById(Long id);

}
