package com.zzw.zzw_final.Repository;

import com.zzw.zzw_final.Dto.Entity.Follow;
import com.zzw.zzw_final.Dto.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findFollowByMember(Member member);
    List<Follow> findAllByMember(Member member);
    List<Follow> findAllByFollowerId(Long followId);
    Follow findFollowByFollowerIdAndMember(Long followerId, Member member); //팔로우한 유저가 지금 내가 팔로워 하고 있는지
    List <Follow> findAllByMemberOrderByFollowerNicknameAsc(Member followerNickname);
    List <Follow> findAllByMemberOrderByFollowNicknameAsc(Member followNickname);
    List <Follow> findAllByFollowerIdOrderByFollowNicknameAsc(Long followerId);


}
