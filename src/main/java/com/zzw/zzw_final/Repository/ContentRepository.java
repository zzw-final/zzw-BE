package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Content;
import com.zzw.zzw_final.Dto.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Content findContentByPost(Post post);
    Content findContentByPostAndPage(Post post, int page);
    List<Content> findAllByPostOrderByPage(Post post);

}
