package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findAllByOrderByLikeNumDesc();
    List<Post> findAllByTitleContaining(String title);
    List<Post> findAllByMember(Member member);
    List<Post> findAllByMemberOrderByCreatedAtDesc(Member member);
    Post findPostById(Long id);
}
