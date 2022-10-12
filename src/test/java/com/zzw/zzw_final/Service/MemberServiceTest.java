package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Grade;
import com.zzw.zzw_final.Dto.Entity.GradeList;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.FollowRepository;
import com.zzw.zzw_final.Repository.GradeRepository;
import com.zzw.zzw_final.Repository.MemberRepository;
import com.zzw.zzw_final.Repository.RefreshTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberServiceTest {

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private FollowRepository followRepository;


    SignupRequestDto signupRequestDto;
    HttpServletRequest request;
    HttpServletRequest request2;
    HttpServletRequest request3;
    HttpServletRequest request4;
    HttpServletRequest request5;
    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        when(request.getHeader("Refresh-Token")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjYwNjU1Njd9.vJtmCwajGZzdsq4W8JsPbL1dymVy7CkYpkA0dl296_g");
        when(request.getHeader("oauth")).thenReturn("kakao");

        signupRequestDto = SignupRequestDto.builder()
                .oauth("kakao")
                .email("zzw@naver.com")
                .nickname("요리왕").build();
    }

    @Test
    void checkMember() {
        //when
        when(tokenProvider.validateToken(request.getHeader("Refresh-Token"))).thenReturn(true);

        request2 = mock(HttpServletRequest.class);
        when(request2.getHeader("Refresh-Token")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjYwNjU1Njd9.vJtmCwajGZzdsq4W8JsPbL1dymVy7CkYpkA0dl296_g");
        when(request2.getHeader("oauth")).thenReturn("kakao");

        request3 = mock(HttpServletRequest.class);
        when(request3.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        when(request3.getHeader("oauth")).thenReturn("kakao");

        request4 = mock(HttpServletRequest.class);
        when(request4.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        when(request4.getHeader("Refresh-Token")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjYwNjU1Njd9.vJtmCwajGZzdsq4W8JsPbL1dymVy7CkYpkA0dl296_g");

        request5 = mock(HttpServletRequest.class);
        when(request5.getHeader("Refresh-Token")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjU3MTk5MTl9.E5N3bzfJo2AlB7xJ_mPJictEaOfQluBC5817_OVF_Jo");
        when(tokenProvider.validateToken(request5.getHeader("Refresh-Token"))).thenReturn(false);

        when(tokenProvider.getUserEmail(request.getHeader("Authorization").substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(request.getHeader("Authorization").substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(tokenProvider.getUserEmail(request.getHeader("Authorization").substring(7)), request.getHeader("oauth"));


        //then
        Assertions.assertEquals(request2.getHeader("Authorization"), null);
        Assertions.assertEquals(request3.getHeader("Refresh-Token"), null);
        Assertions.assertEquals(request4.getHeader("oauth"), null);
        Assertions.assertEquals(tokenProvider.validateToken(request.getHeader("Refresh-Token")), true);
        Assertions.assertEquals(tokenProvider.validateToken(request5.getHeader("Refresh-Token")), false);
        Assertions.assertEquals(email, "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
    }

    @Test
    void postUserNickname() {
        //when
        Member member = new Member(signupRequestDto);
        memberRepository.save(member);

        //then
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertEquals(member.getEmail(), "zzw@naver.com");
        Assertions.assertEquals(member.getNickname(), "요리왕");
    }

    @Test
    void getMember() {
        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        //then
        Assertions.assertEquals(token, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        Assertions.assertEquals(oauth, "kakao");
        Assertions.assertEquals(email, "good9712@nate.com");
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");
    }

    @Test
    void resignMember() {
    }

    @Test
    void getInvalidToken() {
    }

    @Test
    void getUserInfo() {
    }

    @Test
    void getOtherUserInfo() {
    }

    @Test
    void getUserGrade() {
    }
}