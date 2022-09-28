package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MypageUserInfoResponseDto {
    private String nickname;
    private String grade;
    private List<GradeListResponseDto> gradeList;
    private int follow;
    private int follower;
    private String profile;
    private Boolean isFollow;

    public MypageUserInfoResponseDto(Member member, int follow, int follower,
                                     List<GradeListResponseDto> gradeList, Boolean isFollow){
        this.nickname = member.getNickname();
        this.grade = member.getGrade();
        this.profile = member.getProfile();
        this.follow = follow;
        this.follower = follower;
        this.gradeList = gradeList;
        this.isFollow = isFollow;
    }
}
