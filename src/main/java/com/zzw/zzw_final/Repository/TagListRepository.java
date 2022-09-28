package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Post;
import com.zzw.zzw_final.Dto.Entity.TagList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagListRepository extends JpaRepository<TagList, Long> {
    List<TagList> findAllByName(String name);
    List<TagList> findAllByPost(Post post);

    Long countAllByName(String name);

}
