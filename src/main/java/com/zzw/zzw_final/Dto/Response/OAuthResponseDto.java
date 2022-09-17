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

    public OAuthResponseDto(Member kakaoUser, TokenDto tokenDto, String kakaoToken) {
        this.email = kakaoUser.getEmail();
        this.accessToken = tokenDto.getAccessToken();
        this.refreshToken = tokenDto.getRefreshToken();
        this.kakaoToken = kakaoToken;
    }
}
