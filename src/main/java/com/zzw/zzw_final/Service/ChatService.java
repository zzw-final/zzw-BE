package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.ChatMember;
import com.zzw.zzw_final.Dto.Entity.ChatMessage;
import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Request.ChatRequestDto;
import com.zzw.zzw_final.Dto.Response.ChatListResponseDto;
import com.zzw.zzw_final.Dto.Response.ChatMessageResponseDto;
import com.zzw.zzw_final.Dto.Response.ChatRoomResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.ChatMemberRepository;
import com.zzw.zzw_final.Repository.ChatMessageRepository;
import com.zzw.zzw_final.Repository.ChatRoomRepository;
import com.zzw.zzw_final.Repository.MemberRepository;
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

    // 이미 채팅방에 있는 멤버인지 확인
//    public ResponseDto<?> getChatMember(Long eventId, HttpServletRequest request) {
//        ResponseDto<?> chkResponse = validateCheck(request);
//        if (!chkResponse.isSuccess())
//            return chkResponse;
//        Member member = memberRepository.findById(((Member) chkResponse.getData()).getId()).orElse(null);
//        assert member != null;  // 동작할일은 없는 코드
//
//        ChatRoom chatRoom = chatRoomRepository.findById(eventId).orElse(null);
//
//        Optional<ChatMember> chatMember = chatMemberRepository.findByMemberAndChatRoom(member, chatRoom);
//        if (chatMember.isPresent())
//            return ResponseDto.fail("이미 존재하는 회원입니다.");
//        return ResponseDto.success("채팅방에 없는 회원입니다.");
//    }

    // 채팅방 입장
    @Transactional
    public ResponseDto<?> enterChatRoom(ChatRequestDto message, String token, String oauth) {

        // 토큰으로 유저찾기
        String ttoken = token.substring(7);
        String email = tokenProvider.getUserEmail(ttoken);
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(message.getRoomId());

        if (chatRoom == null){
            return ResponseDto.fail(NOTFOUND_ROOM);
        }

        // 이미 채팅방에 있는 멤버면 막아야함.
        ChatMember findChatMember = chatMemberRepository.findChatMemberByChatRoomAndMember(chatRoom, member);
        if (findChatMember != null){
            return ResponseDto.fail(DUPLICATE_ROOM);
        }

        // 없다면 채팅방 멤버목록에 넣기
        ChatMember chatMember = new ChatMember(member, chatRoom);
        chatMemberRepository.save(chatMember);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 - a hh:mm "));
        ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto(member, time);

        // 메세지 보내기
        messageTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessageResponseDto);

        return ResponseDto.success(member.getNickname()+" 입장 성공");
    }

    // 채팅방 나가기
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
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 - a hh:mm "));
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
                ChatListResponseDto chatListResponseDto = new ChatListResponseDto(chatRoom, chatToMember, chatMessage.get(0));
                chatListResponseDtos.add(chatListResponseDto);
            }
        }

        return ResponseDto.success(chatListResponseDtos);
    }
}