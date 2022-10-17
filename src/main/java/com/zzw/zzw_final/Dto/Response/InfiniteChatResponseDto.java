package com.zzw.zzw_final.Dto.Response;

import lombok.Getter;

import java.util.List;
@Getter
public class InfiniteChatResponseDto {
    private Boolean isLast;
    private List<ChatMessageResponseDto> chatList;

    public InfiniteChatResponseDto(Boolean isLast, List<ChatMessageResponseDto> chatList){
        this.isLast = isLast;
        this.chatList = chatList;
    }
}
