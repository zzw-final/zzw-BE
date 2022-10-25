package com.zzw.zzw_final.Dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String nickname;
    private String email;
    private String oauth;

    public SignupRequestDto(String nickname, String email, String oauth){
        this.nickname = nickname;
        this.email = email;
        this.oauth = oauth;
    }
}
