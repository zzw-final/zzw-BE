package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Request.ChatRequestDto;
import com.zzw.zzw_final.Dto.Request.CheckReadMessageRequestDto;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

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
    private final ChatRoomOutRepository chatRoomOutRepository;

    public ResponseDto<?> exitChatRoom(Long roomId, HttpServletRequest request) {

        Member member = memberService.getMember(request);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomId);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime localTime = LocalDateTime.parse(now,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (chatRoom == null){
            return ResponseDto.fail(NOTFOUND_ROOM);
        }

        ChatMember chatMember = chatMemberRepository.findChatMemberByChatRoomAndMember(chatRoom, member);
        if (chatMember == null){
            return ResponseDto.fail(INVALID_MEMBER);
        }
        chatMember.update(true);
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoom(chatRoom);
        for (ChatMessage chatMessage : chatMessages){
            LocalDateTime messageTime = LocalDateTime.parse(chatMessage.getSendTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (localTime.compareTo(messageTime) == -1){
                ChatRoomOut findRoomOut = chatRoomOutRepository.findChatRoomOutByMemberAndChatRoom(member, chatRoom);
                if (findRoomOut != null)
                    findRoomOut.update(chatMessage);
                else{
                    ChatRoomOut chatRoomOut = new ChatRoomOut(member, chatRoom, chatMessage);
                    chatRoomOutRepository.save(chatRoomOut);
                }
            }
        }

        ChatRoomOut findRoomOut = chatRoomOutRepository.findChatRoomOutByMemberAndChatRoom(member, chatRoom);
        if (findRoomOut != null)
            findRoomOut.update(chatMessages.get(chatMessages.size()-1));
        else{
            ChatRoomOut chatRoomOut = new ChatRoomOut(member, chatRoom, chatMessages.get(chatMessages.size()-1));
            chatRoomOutRepository.save(chatRoomOut);
        }

        return ResponseDto.success("????????? ??????");
    }

    public ResponseDto<?> sendMessage(ChatRequestDto message, String token, String oauth) {
        String ttoken = token.substring(7);

        String email = tokenProvider.getUserEmail(ttoken);
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(message.getRoomId());

        if (chatRoom == null){
            return ResponseDto.fail(NOTFOUND_ROOM);
        }
        // ?????? ????????? ?????? (db????????? timestamp ????????? ?????? ?????? ????????????????????? ?????????)
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ChatMessage chatMessage = new ChatMessage(member, chatRoom, message, time);
        chatMessageRepository.save(chatMessage);

        ChatMessageResponseDto chatMessageResponseDto = new ChatMessageResponseDto(member, time, message.getMessage(), chatMessage.getId());

        ChatMember chatMember = chatMemberRepository.findChatMemberByChatRoomAndMemberNot(chatRoom, member);
        chatMember.update(false);
        chatMemberRepository.save(chatMember);

        messageTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), chatMessageResponseDto);
        messageTemplate.convertAndSend("/sub/chat/member/" + chatMember.getMember().getId(), new ChatReadResponseDto(false));

        return ResponseDto.success("success send message!");
    }


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
        ChatRoomOut chatRoomOut = chatRoomOutRepository.findChatRoomOutByMemberAndChatRoom(member, chatRoom);
        int index = (chatRoomOut != null) ? chatMessageList.indexOf(chatRoomOut.getChatMessage()) + 1 : 0;

        for (int i = index; i<chatMessageList.size(); i++) {
            Member getMember = memberRepository.findMemberById(chatMessageList.get(i).getMemberId());

            chatMessageResponseDtos.add(new ChatMessageResponseDto(getMember, chatMessageList.get(i)));
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
                chatMember.update(false);
                chatMemberRepository.save(chatMember);
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
            if(!chatMember.getIsOut()){
                ChatRoom chatRoom = chatRoomRepository.findChatRoomById(chatMember.getChatRoom().getId());
                ChatMember chatToMember = chatMemberRepository.findChatMemberByChatRoomAndMemberNot(chatRoom, member);

                List<ChatMessage> chatMessage = chatMessageRepository.findChatMessageByChatRoomOrderByCreatedAtDesc(chatRoom);

                if (chatMessage.size() != 0){
                    ChatRead chatRead = chatReadRepository.findChatReadByMemberAndChatRoom(member, chatRoom);

                    Long readMessageId = 0L;
                    if (chatRead != null)
                        readMessageId = chatRead.getChatMessage().getId();

                    if (readMessageId < chatMessage.get(0).getId())
                        if (chatToMember == null)
                            chatListResponseDtos.add(new ChatListResponseDto(chatRoom, chatMessage.get(0), false));
                        else
                            chatListResponseDtos.add(new ChatListResponseDto(chatRoom, chatToMember, chatMessage.get(0), false));
                    else
                        if (chatToMember == null)
                            chatListResponseDtos.add(new ChatListResponseDto(chatRoom, chatMessage.get(0), true));
                        else
                            chatListResponseDtos.add(new ChatListResponseDto(chatRoom, chatToMember, chatMessage.get(0), true));
                }
            }
        }

        chatListResponseDtos = chatListResponseDtos.stream()
                .sorted(Comparator.comparing(ChatListResponseDto::getChatTime).reversed()).collect(Collectors.toList());

        return ResponseDto.success(chatListResponseDtos);
    }

    public ResponseDto<?> checkReadMessage(CheckReadMessageRequestDto checkReadMessageRequestDto) {
        Member member = memberRepository.findMemberById(checkReadMessageRequestDto.getUserId());
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(checkReadMessageRequestDto.getRoomId());

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime localTime = LocalDateTime.parse(now,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoom(chatRoom);

        for(ChatMessage chatMessage : chatMessages){
            LocalDateTime messageTime = LocalDateTime.parse(chatMessage.getSendTime(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (localTime.compareTo(messageTime) == -1){
                ChatRead chatRead = chatReadRepository.findChatReadByMemberAndChatRoom(member, chatRoom);
                if (chatRead == null){
                    ChatRead new_chatRead = new ChatRead(chatRoom, chatMessage, member);
                    chatReadRepository.save(new_chatRead);
                    return ResponseDto.success("????????? ?????? ?????? ??????");
                }else{
                    chatRead.update(chatMessage);
                    chatReadRepository.save(chatRead);
                    return ResponseDto.success("????????? ?????? ?????? ??????");
                }
            }
        }

        ChatRead chatRead = chatReadRepository.findChatReadByMemberAndChatRoom(member, chatRoom);
        if (chatRead == null){
            ChatRead new_chatRead = new ChatRead(chatRoom, chatMessages.get(chatMessages.size()-1), member);
            chatReadRepository.save(new_chatRead);
            return ResponseDto.success("????????? ?????? ?????? ??????");
        }else{
            chatRead.update(chatMessages.get(chatMessages.size()-1));
            chatReadRepository.save(chatRead);
            return ResponseDto.success("????????? ?????? ?????? ??????");
        }
    }

    public ResponseDto<?> isNewMessage(HttpServletRequest request) {
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = null;
        if (result.isSuccess()){
            member = (Member) result.getData();
        }

        List<ChatMember> chatMembers = chatMemberRepository.findAllByMember(member);
        for(ChatMember chatMember : chatMembers){

            ChatRead chatRead = chatReadRepository.findChatReadByMemberAndChatRoom(member, chatMember.getChatRoom());
            List<ChatMessage> chatMessage = chatMessageRepository.findChatMessageByChatRoomOrderByCreatedAtDesc(chatMember.getChatRoom());

            Long readMessageId = 0L;
            if (chatRead != null)
                readMessageId = chatRead.getChatMessage().getId();

            if (chatMessage.size() != 0){
                if (readMessageId < chatMessage.get(0).getId())
                    return ResponseDto.success(new ChatReadResponseDto(false));
            }
        }
        return ResponseDto.success(new ChatReadResponseDto(true));
    }

    public ResponseDto<?> findMember(HttpServletRequest request, String nickname) {
        Member member = memberService.getMember(request);
        List<MemberListResponseDto> memberListResponseDtos = new ArrayList<>();

        if (nickname != null){
            List<Member> members = memberRepository.findAllByNicknameContaining(nickname);

            for(Member findMember : members){
                if (findMember != member)
                    memberListResponseDtos.add(new MemberListResponseDto(findMember));
            }
        }else{
            memberListResponseDtos.add(new MemberListResponseDto(memberRepository.findMemberById(1L)));
            memberListResponseDtos.add(new MemberListResponseDto(memberRepository.findMemberById(48L)));
            memberListResponseDtos.add(new MemberListResponseDto(memberRepository.findMemberById(145L)));
            memberListResponseDtos.add(new MemberListResponseDto(memberRepository.findMemberById(1195L)));
            memberListResponseDtos.add(new MemberListResponseDto(memberRepository.findMemberById(1481L)));
        }

        return ResponseDto.success(memberListResponseDtos);
    }
}