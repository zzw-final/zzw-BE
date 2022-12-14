package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.CheckReadMessageRequestDto;
import com.zzw.zzw_final.Service.ChatService;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    @GetMapping("/api/mypage/{user_id}/chat")
    public ResponseDto<?> getChatRoom(@PathVariable Long user_id, HttpServletRequest request){
        return chatService.getChatRoom(user_id, request);
    }

    @GetMapping ("/api/chat/message/{roomId}")
    public ResponseDto<?> getMessageLog (@PathVariable Long roomId, HttpServletRequest request) {
        return chatService.getMessage(roomId, request);
    }

    @DeleteMapping("/api/chat/member/{roomId}")
    public ResponseDto<?> exitChatRoom (@PathVariable Long roomId, HttpServletRequest request) {
        return chatService.exitChatRoom(roomId, request);
    }

    @GetMapping("/api/chat")
    public ResponseDto<?> getUserChatList(HttpServletRequest request){
        return chatService.getUserChatList(request);
    }

    @PutMapping("/api/chat/newmessage")
    public ResponseDto<?> checkReadMessage(@RequestBody CheckReadMessageRequestDto checkReadMessageRequestDto){
        return chatService.checkReadMessage(checkReadMessageRequestDto);
    }

    @GetMapping("/api/chat/alarm")
    public ResponseDto<?> isNewMessage(HttpServletRequest request){
        return chatService.isNewMessage(request);
    }

    @GetMapping("/api/chat/member")
    public ResponseDto<?> findMember(HttpServletRequest request,@RequestParam(name = "nickname", required = false)String nickname){
        return chatService.findMember(request, nickname);
    }
}