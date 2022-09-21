package com.zzw.zzw_final.Dto.Response;

import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.TokenDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthResponseDto {
    private String email;
    private String accessToken;
    private String refreshToken;
    private String kakaoToken;
    private Long userId;
    private String nickname;
    private String profile;

    public OAuthResponseDto(Member member, TokenDto tokenDto, String oauthToken) {
        this.email = member.getEmail();
        this.accessToken = tokenDto.getAccessToken();
        this.refreshToken = tokenDto.getRefreshToken();
        this.kakaoToken = oauthToken;
        this.userId = member.getId();
        this.nickname = member.getNickname();
        this.profile = member.getProfile();
    }

}
