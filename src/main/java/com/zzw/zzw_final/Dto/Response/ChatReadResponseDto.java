package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;

@Getter
public class ChatReadResponseDto {
    private Boolean isRead;

    public ChatReadResponseDto(Boolean isRead){
        this.isRead = isRead;
    }
}
