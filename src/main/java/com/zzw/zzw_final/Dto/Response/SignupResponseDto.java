package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.TokenDto;
import lombok.Getter;

@Getter
public class SignupResponseDto {
    private String email;
    private String nickname;
    private String grade;
    private Long userId;
    private String profile;
    private String accessToken;
    private String refreshToken;
    private String invalidTime;
    private String oauth;

    public SignupResponseDto(Member member, TokenDto tokenDto, String invalidTime) {
        this.email = member.getEmail();
        this.oauth = member.getOauth();
        this.nickname = member.getNickname();
        this.grade = member.getGrade();
        this.userId = member.getId();
        this.profile = member.getProfile();
        this.accessToken = tokenDto.getAccessToken();
        this.refreshToken = tokenDto.getRefreshToken();
        this.invalidTime = invalidTime;
    }
}
