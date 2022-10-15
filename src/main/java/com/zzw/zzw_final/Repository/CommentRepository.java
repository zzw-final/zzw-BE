package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Comment;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostOrderByCreatedAtDesc(Post post);
    Comment findPostById(Long id);
    Comment findCommentById(Long id);
    List<Comment> findAllByMember(Member member);

}
