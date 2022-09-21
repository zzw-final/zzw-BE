package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Comment;
import com.zzw.zzw_final.Dto.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findPostById(Post post);
    List<Comment> findAllByPost(Post post);

}
