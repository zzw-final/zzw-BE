package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findAllByEmail(String email);
    Member findMemberById(Long id);
    List<Member> findAllByNicknameContaining(String nickname);
    Member findMemberByEmailAndOauth(String email, String oauth);
    Optional<Member> findMemberByOauthAndEmail(String oauth, String email);
    Optional<Member> findByNickname(String nickname);
    List<Member> findAllByOrderByNicknameAsc();
}
