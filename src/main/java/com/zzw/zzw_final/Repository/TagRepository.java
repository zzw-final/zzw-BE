package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findTagByName(String name);
    List<Tag> findAllByOrderByCountDesc();
}
