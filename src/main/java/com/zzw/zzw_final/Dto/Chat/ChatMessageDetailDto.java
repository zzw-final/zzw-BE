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

    public static ChatMessageDetailDto toChatMessageDetailDTO(ChatMessage chatMessage){
        ChatMessageDetailDto chatMessageDetailDTO = new ChatMessageDetailDto();

        chatMessageDetailDTO.setChatId(chatMessage.getId());

        chatMessageDetailDTO.setChatRoomId(chatMessage.getChatRoom().getId());
        chatMessageDetailDTO.setRoomId(chatMessage.getChatRoom().getRoomId());

        chatMessageDetailDTO.setWriter(chatMessage.getWriter());
        chatMessageDetailDTO.setMessage(chatMessage.getMessage());

        return chatMessageDetailDTO;

    }

}