package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Repository.*;
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

    @BeforeEach
    public void setup() {
        
    }

    @Test
    void exitChatRoom() {
    }

    @Test
    void sendMessage() {
    }

    @Test
    void getMessage() {
    }

    @Test
    void getChatRoom() {
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