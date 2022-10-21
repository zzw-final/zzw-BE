package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Member;
import lombok.Getter;

@Getter
public class MemberListResponseDto {
    private Long userId;
    private String nickname;
    private String profile;
    private String grade;

    public MemberListResponseDto(Member member){
        this.userId = member.getId();
        this.nickname = member.getNickname();
        this.profile = member.getProfile();
        this.grade = member.getGrade();
    }
}
