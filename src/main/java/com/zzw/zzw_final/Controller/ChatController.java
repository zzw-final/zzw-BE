package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Chat.ChatRequestDto;
import com.zzw.zzw_final.Dto.Chat.ChatService;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;

    // 채팅메세지 보내기
    @MessageMapping("/chat/message")
    public ResponseDto<?> message(ChatRequestDto message, @Header("Authorization") String token,
                                  @Header("oauth") String oauth) {
        return chatService.sendMessage(message, token, oauth);
    }

    // 채팅방 입장
    @MessageMapping("/chat/enter")
    public ResponseDto<?> enterChatRoom(ChatRequestDto message, @Header("Authorization") String token,
                                        @Header("oauth") String oauth) {
        return chatService.enterChatRoom(message, token, oauth);
    }
}