package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.ChatMember;
import com.zzw.zzw_final.Dto.Entity.ChatMessage;
import com.zzw.zzw_final.Dto.Entity.ChatRoom;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.ChatMessageResponseDto;
import com.zzw.zzw_final.Dto.Response.ChatRoomResponseDto;
import com.zzw.zzw_final.Repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatServiceTest {

    @MockBean
    private TokenProvider tokenProvider;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private MemberRepository memberRepository;
    @MockBean
    private MemberService memberService;
    @Autowired
    private ChatMemberRepository chatMemberRepository;
    @Autowired
    private ChatReadRepository chatReadRepository;

    HttpServletRequest request;
    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        when(request.getHeader("Refresh-Token")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjYwNjU1Njd9.vJtmCwajGZzdsq4W8JsPbL1dymVy7CkYpkA0dl296_g");
        when(request.getHeader("oauth")).thenReturn("kakao");
    }

    @Test
    void exitChatRoom() {
        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        ChatRoom null_chatRoom = chatRoomRepository.findChatRoomById(1L);
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(3332L);

        ChatMember chatMember = chatMemberRepository.findChatMemberByChatRoomAndMember(chatRoom, member);
        ChatMember null_chatMember = chatMemberRepository.findChatMemberByChatRoomAndMember(null_chatRoom, member);


        //then
        Assertions.assertEquals(token, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        Assertions.assertEquals(oauth, "kakao");
        Assertions.assertEquals(email, "good9712@nate.com");
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertNull(null_chatRoom);
        Assertions.assertNotNull(chatRoom);
        Assertions.assertNotNull(chatMember);
        Assertions.assertNull(null_chatMember);
    }

    @Test
    void getMessage() {
        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(3332L);
        ChatMember chatMember = chatMemberRepository.findChatMemberByChatRoomAndMember(chatRoom, member);

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);
        List<ChatMessageResponseDto> chatMessageResponseDtos = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessageList) {
            Member getMember = chatMessage.getMember();
            chatMessageResponseDtos.add(new ChatMessageResponseDto(getMember, chatMessage));
        }


        //then
        Assertions.assertEquals(token, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        Assertions.assertEquals(oauth, "kakao");
        Assertions.assertEquals(email, "good9712@nate.com");
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertNotNull(chatRoom);
        Assertions.assertNotNull(chatMember);
        Assertions.assertEquals(chatMember.getMember(), member);
        Assertions.assertEquals(chatMember.getChatRoom(), chatRoom);
        Assertions.assertEquals(chatMessageList.size(), chatMessageResponseDtos.size());
    }

    @Test
    void getChatRoom() {
        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        Member chatToMember = memberRepository.findMemberById(1482L);

        ChatRoom chatRoom = new ChatRoom();
        chatRoomRepository.save(chatRoom);

        ChatMember chatMember = new ChatMember(member, chatRoom);
        ChatMember chatMember1 = new ChatMember(chatToMember, chatRoom);
        chatMemberRepository.save(chatMember1);
        chatMemberRepository.save(chatMember);

        ChatRoomResponseDto chatRoomResponseDto = new ChatRoomResponseDto(chatRoom.getId());

        //then
        Assertions.assertEquals(token, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        Assertions.assertEquals(oauth, "kakao");
        Assertions.assertEquals(email, "good9712@nate.com");
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertNotEquals(member.getId(), chatToMember.getId());
        Assertions.assertNotNull(chatRoom);
        Assertions.assertEquals(chatMember.getMember(), member);
        Assertions.assertEquals(chatMember1.getMember(), chatToMember);
        Assertions.assertEquals(chatMember.getChatRoom(), chatRoom);
        Assertions.assertEquals(chatMember1.getChatRoom(), chatRoom);
        Assertions.assertEquals(chatRoomResponseDto.getRoomId(), chatRoom.getId());
    }

    @Test
    void getUserChatList() {
    }

    @Test
    void checkReadMessage() {
    }

    @Test
    void isNewMessage() {
    }
}