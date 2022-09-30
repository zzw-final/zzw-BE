package com.zzw.zzw_final.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OauthUserDto {
    private Long id;
    private String email;

    public OauthUserDto(Long id, String email){
        this.id = id;
        this.email = email;
    }
}