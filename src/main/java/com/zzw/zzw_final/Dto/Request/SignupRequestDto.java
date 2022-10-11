package com.zzw.zzw_final.Dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupRequestDto {
    private String nickname;
    private String email;
    private String oauth;
}
