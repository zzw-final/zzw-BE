package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Follow;
import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findAllByMember(Member member);
    List<Follow> findAllByFollowerId(Long followId);
}
