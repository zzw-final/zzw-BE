package com.zzw.zzw_final.Dto.Chat;

import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDetailDto {
    private Long chatRoomId;
    private String chatMentor;
    private String roomId;
    private String name;

    public static ChatRoomDetailDto toChatRoomDetailDTO(ChatRoom chatRoom){
        ChatRoomDetailDto chatRoomDetailDTO = new ChatRoomDetailDto();

        chatRoomDetailDTO.setChatRoomId(chatRoom.getId());
        chatRoomDetailDTO.setChatMentor(chatRoom.getChatMentor());
        chatRoomDetailDTO.setRoomId(chatRoom.getRoomId());
        chatRoomDetailDTO.setName(chatRoom.getRoomName());

        return chatRoomDetailDTO;
    }

}