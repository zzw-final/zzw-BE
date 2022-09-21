package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.GradeList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeListRepository extends JpaRepository<GradeList, Long> {
    GradeList findGradeListById(Long id);
}
