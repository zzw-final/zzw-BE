package com.zzw.zzw_final.Dto.Chat;

import com.zzw.zzw_final.Dto.Entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDetailDto {

    private Long chatId;
    private Long chatRoomId;

    private String roomId;
    private String writer;
    private String message;

    public ChatMessageDetailDto(ChatMessage chatMessage) {
        this.chatId = chatMessage.getId();
        this.chatRoomId = chatMessage.getChatRoom().getId();
        this.roomId = chatMessage.getChatRoom().getRoomId();
        this.writer = chatMessage.getWriter();
        this.message = chatMessage.getMessage();
    }

}