package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.ChatRequestDto;
import com.zzw.zzw_final.Service.ChatService;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatMessageController {

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public ResponseDto<?> message(ChatRequestDto message, @Header("Authorization") String token,
                                  @Header("oauth") String oauth) {
        return chatService.sendMessage(message, token, oauth);
    }

}