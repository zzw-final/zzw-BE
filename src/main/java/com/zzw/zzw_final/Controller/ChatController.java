package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Chat.ChatMessageDetailDto;
import com.zzw.zzw_final.Dto.Chat.ChatMessageSaveDto;
import com.zzw.zzw_final.Dto.Entity.ChatMessage;
import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import com.zzw.zzw_final.Repository.ChatMessageRepository;
import com.zzw.zzw_final.Repository.ChatRoomRepository;
import com.zzw.zzw_final.Service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    //Client 가 SEND 할 수 있는 경로
    //stompConfig 에서 설정한 applicationDestinationPrefixes 와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDetailDto message) {   // 누군가가 DM을 처음 보냈을 때 ----------
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");


        List<ChatMessageDetailDto> chatList = chatService.findAllChatByRoomId(message.getRoomId());
        if(chatList != null){
            for(ChatMessageDetailDto c : chatList ){
                message.setWriter(c.getWriter());
                message.setMessage(c.getMessage());
            }
        }

        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);

        ChatRoom chatRoomEntity= chatRoomRepository.findByRoomId(message.getRoomId());
        ChatMessageSaveDto chatMessageSaveDto = new ChatMessageSaveDto(message.getRoomId(),message.getWriter(), message.getMessage());
        chatMessageRepository.save(ChatMessage.toChatEntity(chatMessageSaveDto,chatRoomEntity));
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDetailDto message) {
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);

        // DB에 채팅내용 저장
        ChatRoom chatRoomEntity= chatRoomRepository.findByRoomId(message.getRoomId());
        ChatMessageSaveDto chatMessageSaveDTO = new ChatMessageSaveDto(message.getRoomId(),message.getWriter(), message.getMessage());
        chatMessageRepository.save(ChatMessage.toChatEntity(chatMessageSaveDTO,chatRoomEntity));
    }
//    @MessageMapping 을 통해 WebSocket 으로 들어오는 메세지 발행을 처리한다.
//    Client 에서는 prefix 를 붙여 "/pub/chat/enter"로 발행 요청을 하면
//    Controller 가 해당 메세지를 받아 처리하는데,
//    메세지가 발행되면 "/sub/chat/room/[roomId]"로 메세지가 전송되는 것을 볼 수 있다.
//    Client 에서는 해당 주소를 SUBSCRIBE 하고 있다가 메세지가 전달되면 화면에 출력한다.
//    이때 /sub/chat/room/[roomId]는 채팅방을 구분하는 값이다.
//    기존의 핸들러 ChatHandler 의 역할을 대신 해주므로 핸들러는 없어도 된다.
}