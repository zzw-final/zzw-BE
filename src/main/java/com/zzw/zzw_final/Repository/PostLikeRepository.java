package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.Post;
import com.zzw.zzw_final.Dto.Entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    PostLike findPostLikesByPostAndMember(Post post, Member member);
    List<PostLike> findPostLikesByPost(Post post);
}
