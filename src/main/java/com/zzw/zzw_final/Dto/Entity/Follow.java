package com.zzw.zzw_final.Dto.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private Long followerId;   // -> 팔로워

    @Column
    private String followNickname;

    @Column
    private String followerNickname;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;   // 팔로우 하는 주인공



    public Follow(Member followerMember ,Member followMember) {
        this.followerId = followerMember.getId();
        this.member = followMember ;
        this.followNickname = followMember.getNickname();
        this.followerNickname = followerMember.getNickname();
    }

}
