package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.TokenDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthResponseDto {
    private String email;
    private Boolean isFirst;
    private String nickname;
    private String grade;
    private Long userId;
    private String profile;
    private String accessToken;
    private String refreshToken;
    private String oauthToken;

    public OAuthResponseDto(String email, String oauthToken) {
        this.email = email;
        this.oauthToken = oauthToken;
        this.isFirst = true;
    }

    public OAuthResponseDto(Member member, TokenDto tokenDto, String oauthToken){
        this.email = member.getEmail();
        this.isFirst = false;
        this.nickname = member.getNickname();
        this.grade = member.getGrade();
        this.userId = member.getId();
        this.profile = member.getProfile();
        this.accessToken = tokenDto.getAccessToken();
        this.refreshToken = tokenDto.getRefreshToken();
        this.oauthToken = oauthToken;
    }

}
