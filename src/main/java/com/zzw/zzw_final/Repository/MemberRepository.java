package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Member findMemberByEmail(String email);
    Member findMemberById(Long id);
    List<Member> findAllByNicknameContaining(String nickname);
}
