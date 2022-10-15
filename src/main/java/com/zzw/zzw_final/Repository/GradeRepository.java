package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Grade;
import com.zzw.zzw_final.Dto.Entity.GradeList;
import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByMember(Member member);
    Grade findGradeByMemberAndGradeList(Member member, GradeList gradeList);

}
