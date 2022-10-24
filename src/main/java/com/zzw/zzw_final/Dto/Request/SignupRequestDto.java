package com.zzw.zzw_final.Dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String nickname;
    private String email;
    private String oauth;
}
