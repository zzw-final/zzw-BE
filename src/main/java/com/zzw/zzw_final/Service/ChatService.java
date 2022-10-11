package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Request.ChatRequestDto;
import com.zzw.zzw_final.Dto.Request.CheckReadMessageRequestDto;
import com.zzw.zzw_final.Dto.Response.ChatListResponseDto;
import com.zzw.zzw_final.Dto.Response.ChatMessageResponseDto;
import com.zzw.zzw_final.Dto.Response.ChatRoomResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.*;
import com.zzw.zzw_final.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.zzw.zzw_final.Dto.ErrorCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessageSendingOperations messageTemplate;
    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatReadRepository chatReadRepository;

    @Transactional
    public ResponseDto<?> exitChatRoom(Long roomId, HttpServletRequest request) {

        Member member = memberService.getMember(request);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomId);

        if (chatRoom == null){
            return ResponseDto.fail(NOTFOUND_ROOM);
        }

        ChatMember chatMember = chatMemberRepository.findChatMemberByChatRoomAndMember(chatRoom, member);
        if (chatMember == null){
            return ResponseDto.fail(INVALID_MEMBER);
        }

        chatMemberRepository.delete(chatMember);

        return ResponseDto.success("나가기 완료");
    }

    // 메세지 보내기
    @Transactional
    public ResponseDto<?> sendMessage(ChatRequestDto message, String token, String oauth) {
        String ttoken = token.substring(7);

        // 토큰으로 유저찾기
        String email = tokenProvider.getUserEmail(ttoken);
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(message.getRoomId());

        if (chatRoom == null){
            return ResponseDto.fail(NOTFOUND_ROOM);
        }
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto(member, time, message.getMessage());

        // 메세지 보내기
        messageTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessageResponseDto);

        // 보낸 메세지 저장 (db바뀔때 timestamp 없애고 위의 값을 저장하는것으로 바꾸기)
        ChatMessage chatMessage = new ChatMessage(member, chatRoom, message, time);

        chatMessageRepository.save(chatMessage);

        return ResponseDto.success("success send message!");
    }


    // 기존 채팅방 메세지들 불러오기
    public ResponseDto<?> getMessage(Long roomId, HttpServletRequest request) {

        Member member = memberService.getMember(request);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomId);

        if (chatRoom == null){
            return ResponseDto.fail(NOTFOUND_ROOM);
        }

        ChatMember chatMember = chatMemberRepository.findChatMemberByChatRoomAndMember(chatRoom, member);
        if (chatMember == null){
            return ResponseDto.fail(INVALID_MEMBER);
        }

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);
        List<ChatMessageResponseDto> chatMessageResponseDtos = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessageList) {
            Member getMember = chatMessage.getMember();

            chatMessageResponseDtos.add(new ChatMessageResponseDto(getMember, chatMessage));
        }
        return ResponseDto.success(chatMessageResponseDtos);
    }

    public ResponseDto<?> getChatRoom(Long user_id, HttpServletRequest request) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = null;
        if (result.isSuccess()){
            member = (Member) result.getData();
        }

        Member chatToMember = memberRepository.findMemberById(user_id);

        List<ChatMember> chatMembers = chatMemberRepository.findAllByMember(member);

        for (ChatMember chatMember : chatMembers){
            ChatRoom chatRoom = chatMember.getChatRoom();
            ChatMember isChatRoom = chatMemberRepository.findChatMemberByChatRoomAndMember(chatRoom, chatToMember);
            if (isChatRoom != null){
                ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto(isChatRoom.getChatRoom().getId());
                return ResponseDto.success(chatRoomResponseDto);
            }
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoomRepository.save(chatRoom);

        ChatMember chatMember = new ChatMember(member, chatRoom);
        ChatMember chatMember1 = new ChatMember(chatToMember, chatRoom);
        chatMemberRepository.save(chatMember1);
        chatMemberRepository.save(chatMember);

        ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto(chatRoom.getId());

        return ResponseDto.success(chatRoomResponseDto);
    }

    public ResponseDto<?> getUserChatList(HttpServletRequest request) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = null;
        if (result.isSuccess()){
            member = (Member) result.getData();
        }

        List<ChatMember> chatMembers = chatMemberRepository.findAllByMember(member);
        List<ChatListResponseDto> chatListResponseDtos = new ArrayList<>();

        for(ChatMember chatMember : chatMembers){
            ChatRoom chatRoom = chatMember.getChatRoom();
            ChatMember chatToMember = chatMemberRepository.findChatMemberByChatRoomAndMemberNot(chatRoom, member);
            List<ChatMessage> chatMessage = chatMessageRepository.findChatMessageByChatRoomOrderByCreatedAtDesc(chatRoom);

            if (chatMessage.size() != 0){
                ChatRead chatRead = chatReadRepository.findChatReadByMemberAndChatRoom(member, chatRoom);
                if (chatRead.getChatMessage().getId() < chatMessage.get(0).getId())
                    chatListResponseDtos.add(new ChatListResponseDto(chatRoom, chatToMember, chatMessage.get(0), false));
                else
                    chatListResponseDtos.add(new ChatListResponseDto(chatRoom, chatToMember, chatMessage.get(0), true));
            }
        }

        return ResponseDto.success(chatListResponseDtos);
    }

    public ResponseDto<?> checkReadMessage(HttpServletRequest request, CheckReadMessageRequestDto checkReadMessageRequestDto) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = null;
        if (result.isSuccess()){
            member = (Member) result.getData();
        }

        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(checkReadMessageRequestDto.getRoomId());
        ChatRead checkChatRead = chatReadRepository.findChatReadByMemberAndChatRoom(member, chatRoom);
        ChatMessage chatMessage = chatMessageRepository.findChatMessageByIdAndChatRoom(checkReadMessageRequestDto.getMessageId(), chatRoom);

        if (chatMessage == null)
            return ResponseDto.fail(NOTFOUND_MESSAGE);

        if (checkChatRead == null){
            ChatRead chatRead = new ChatRead(chatRoom, chatMessage, member);
            chatReadRepository.save(chatRead);
        }else{
            checkChatRead.update(chatMessage);
            chatReadRepository.save(checkChatRead);
        }

        return ResponseDto.success("메세지 읽음 처리 완료");
    }
}