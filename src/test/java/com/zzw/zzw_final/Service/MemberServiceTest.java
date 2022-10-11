package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletResponse;

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
//
//        SignupRequestDto signupRequestDto = new SignupRequestDto();
//        signupRequestDto.setNickname("jeeyeon");
//        signupRequestDto.setEmail("good9712@nate.com");
//
//        ResponseDto<?> responseDto = memberService.postUserNickname(,signupRequestDto);
//
//        Member loginMember = new Member(signupRequestDto);
//
//        Assertions.assertEquals(loginMember.getNickname(), "jeeyeon");
//        Assertions.assertEquals(loginMember.getEmail(), "good9712@nate.com");
    }

}