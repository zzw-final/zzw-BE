package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Chat.ChatMessageDetailDto;
import com.zzw.zzw_final.Dto.Entity.ChatMessage;
import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import com.zzw.zzw_final.Repository.ChatMessageRepository;
import com.zzw.zzw_final.Repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    public List<ChatMessageDetailDto> findAllChatByRoomId(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoom(chatRoom);

        List<ChatMessageDetailDto> chatMessageDetailDtos = new ArrayList<>();

        for(ChatMessage chatMessage : chatMessages){
            chatMessageDetailDtos.add(new ChatMessageDetailDto(chatMessage));
        }

        return chatMessageDetailDtos;
    }
}
