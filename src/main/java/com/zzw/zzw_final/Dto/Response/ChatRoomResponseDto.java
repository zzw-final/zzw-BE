package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;

@Getter
public class ChatRoomResponseDto {
    private Long roomId;

    public ChatRoomResponseDto(Long id) {
        this.roomId = id;
    }
}
