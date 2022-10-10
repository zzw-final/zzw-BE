package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Service.ChatService;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    //채팅방 생성
    @GetMapping("/api/mypage/{user_id}/chat")
    public ResponseDto<?> getChatRoom(@PathVariable Long user_id, HttpServletRequest request){
        return chatService.getChatRoom(user_id, request);
    }

    // 채팅메세지 불러오기
    @GetMapping ("/api/chat/message/{roomId}")
    public ResponseDto<?> getMessageLog (@PathVariable Long roomId, HttpServletRequest request) {
        return chatService.getMessage(roomId, request);
    }

    // 채팅방 나가기
    @DeleteMapping("/api/chat/member/{roomId}")
    public ResponseDto<?> exitChatRoom (@PathVariable Long roomId, HttpServletRequest request) {
        return chatService.exitChatRoom(roomId, request);
    }
}