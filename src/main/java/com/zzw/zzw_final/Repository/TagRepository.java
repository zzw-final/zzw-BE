package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findTagByName(String name);
}
