package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@Import({TokenProvider.class, MemberRepository.class})
class MemberServiceTest {

    @MockBean
    TokenProvider tokenProvider;

    @MockBean
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Test
    public void postUserNickname(){

        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setNickname("jeeyeon");
        signupRequestDto.setEmail("good9712@nate.com");

        Member loginMember = new Member(signupRequestDto);

        TokenDto tokenDto = tokenProvider.generateTokenDto(loginMember);



    }

}