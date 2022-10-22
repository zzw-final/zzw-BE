package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.ProfileList;
import com.zzw.zzw_final.Repository.MemberRepository;
import com.zzw.zzw_final.Repository.ProfileListRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.zzw.zzw_final.Dto.ErrorCode.MEMBER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileServiceTest {

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private ProfileListRepository profileListRepository;

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

//    @Test
//    public void getMemberProfile() {
//
//        //when
//        List<ProfileList> profileLists = profileListRepository.findAll();
//        List<ProfileResponseDto> profileResponseDtos = new ArrayList<>();
//        for(ProfileList profileList : profileLists){
//            profileResponseDtos.add(new ProfileResponseDto(profileList));
//        }
//
//        //then
//        Assertions.assertEquals(member.getOauth(), "kakao");
//        Assertions.assertEquals(member.getEmail(), "zzw@naver.com");
//        Assertions.assertEquals(member.getNickname(), "요리왕");
//        Assertions.assertEquals(profileLists.size(),1);
//        Assertions.assertEquals(profileLists.get(0).getProfile(),"https://zzwimage.s3.ap-northeast-2.amazonaws.com/profile/%EB%8C%80%EC%A7%80+1.png");
//        Assertions.assertEquals(profileResponseDtos.size(),1);
//
//
//    }

    @Test
    public void updateMemberProfile() {

        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);


        ProfileList profileList = profileListRepository.findProfileListById(9L);

        member.updateProfile(profileList.getProfile());
        memberRepository.save(member);

        //then
        Assertions.assertEquals(token, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        Assertions.assertEquals(oauth, "kakao");
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");

        Assertions.assertEquals(profileList.getProfile(),"https://zzwimage.s3.ap-northeast-2.amazonaws.com/profile/%EB%8C%80%EC%A7%80+9.png");
        Assertions.assertEquals(member.getProfile(),"https://zzwimage.s3.ap-northeast-2.amazonaws.com/profile/%EB%8C%80%EC%A7%80+9.png");

    }

}