package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowResponseDto {
    private Long userId;
    private String nickname;
    private String grade;
    private String profile;
    private Boolean isFollow;

    public FollowResponseDto(Member member1) {
        this.userId = member1.getId();
        this.nickname = member1.getNickname();
        this.grade = member1.getGrade();
        this.profile = member1.getProfile();
        this.isFollow = false;
    }

    public FollowResponseDto(Member member2, boolean isFollow) {
        this.userId = member2.getId();
        this.nickname = member2.getNickname();
        this.grade = member2.getGrade();
        this.profile = member2.getProfile();
        this.isFollow = isFollow;
    }
}
