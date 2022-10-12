package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Follow;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.FollowResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.FollowRepository;
import com.zzw.zzw_final.Repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class FollowServiceTest {

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private FollowRepository followRepository;

    @MockBean
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    HttpServletRequest request;
    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        when(request.getHeader("Refresh-Token")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjYwNjU1Njd9.vJtmCwajGZzdsq4W8JsPbL1dymVy7CkYpkA0dl296_g");
        when(request.getHeader("oauth")).thenReturn("kakao");

    }

    @Test
    void follow() {
        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        Member followMember = memberRepository.findMemberById(48L);

        Follow followUser = new Follow(member, followMember);
        followRepository.save(followUser);

        //then
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertEquals(followMember.getEmail(), "hyundo717@kakao.com");
        Assertions.assertEquals(followMember.getOauth(), "kakao");
        Assertions.assertEquals(followUser.getFollowerId(), member.getId());
        Assertions.assertEquals(followUser.getMember(), followMember);
    }

    @Test
    void getFollow() {
        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member loginMember = memberRepository.findMemberByEmailAndOauth(email, oauth);

        List<Member> members = new ArrayList<>();
        List<Follow> followerlist = followRepository.findAllByFollowerIdOrderByFollowNicknameAsc(loginMember.getId());

        for (Follow follower : followerlist) {
            Member member2 = memberRepository.findMemberById(follower.getMember().getId());
            members.add(member2);
        }

        List<FollowResponseDto> followerResponseDtos = new ArrayList<>();

        for (Member member : members)
            followerResponseDtos.add(new FollowResponseDto(member));


        //then
        Assertions.assertEquals(loginMember.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(loginMember.getOauth(), "kakao");
        Assertions.assertEquals(followerlist.size(), members.size());
        Assertions.assertEquals(members.size(), followerResponseDtos.size());
    }

    @Test
    void getFollower() {
    }

    @Test
    void getOthersFollow() {
    }

    @Test
    void getOthersFollower() {
    }
}