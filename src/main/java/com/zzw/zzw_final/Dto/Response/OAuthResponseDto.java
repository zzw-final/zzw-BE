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
    private Boolean isDuplicate;
    private String nickname;
    private String grade;
    private Long userId;
    private String profile;
    private String accessToken;
    private String refreshToken;
    private String oauthToken;
    private String oauth;
    private String invalidTime;

    public OAuthResponseDto(String email, String oauthToken, String oauth, Boolean isDuplicate) {
        this.email = email;
        this.oauthToken = oauthToken;
        this.oauth = oauth;
        this.isFirst = true;
        this.isDuplicate = isDuplicate;
    }

    public OAuthResponseDto(Member member, TokenDto tokenDto, String oauthToken,
                            String oauth, String invalidTime){
        this.email = member.getEmail();
        this.oauth = oauth;
        this.isFirst = false;
        this.nickname = member.getNickname();
        this.grade = member.getGrade();
        this.userId = member.getId();
        this.profile = member.getProfile();
        this.accessToken = tokenDto.getAccessToken();
        this.refreshToken = tokenDto.getRefreshToken();
        this.oauthToken = oauthToken;
        this.invalidTime = invalidTime;
    }

}
