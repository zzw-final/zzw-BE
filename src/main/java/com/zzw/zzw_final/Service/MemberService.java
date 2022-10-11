package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Follow;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.ProfileList;
import com.zzw.zzw_final.Dto.Entity.RefreshToken;
import com.zzw.zzw_final.Dto.Request.IntegrationMemberRequestDto;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.IntegrationResponseDto;
import com.zzw.zzw_final.Dto.Response.ProfileResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zzw.zzw_final.Dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FollowRepository followRepository;
    private final ProfileListRepository profileListRepository;

    public ResponseDto<?> checkMember(HttpServletRequest request){

        //헤더에 리프레시 토큰이 안 왔을 경우 오류 값 반환
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail(NULL_TOKEN);
        }

        //헤더에 어세스 토큰이 안 왔을 경우 오류 값 반환
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail(NULL_TOKEN);
        }

        //헤더에 리프레시 토큰이 유효시간이 지났을 경우 오류 값 반환
        if(!tokenProvider.validateToken(request.getHeader("Refresh-Token")))
            return ResponseDto.fail(INVALID_TOKEN);

        if(null == request.getHeader("oauth")){
            return ResponseDto.fail(NULL_OAUTH);
        }

        //헤더에 정상적으로 토큰이 전달 됐을 경우
        String email = tokenProvider.getUserEmail(request.getHeader("Authorization").substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, request.getHeader("oauth"));

        return ResponseDto.success(member);
    }

    public ResponseDto<?> postUserNickname(HttpServletResponse response, SignupRequestDto requestDto) {

        Member member = new Member(requestDto);
        memberRepository.save(member);

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        response.addHeader("Authorization", tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        IntegrationResponseDto responseDto = new IntegrationResponseDto(member, tokenDto);

        return ResponseDto.success(responseDto);
    }

    // 토큰을 보내줬는지 안보내줬는지 확인하는 함수
    public Member getMember(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        if (token != null){
            String email = tokenProvider.getUserEmail(token.substring(7));
            Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);
            return member;
        }

        return null;
    }

    public ResponseDto<?> resignMember(Long member_id) {
        Member member = memberRepository.findMemberById(member_id);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(member);
        if (refreshToken != null){
            refreshTokenRepository.delete(refreshToken.get());
        }
        List<Follow> followList = followRepository.findAllByFollowerId(member_id);
        for (Follow follow : followList){
            followRepository.delete(follow);
        }
        memberRepository.delete(member);

        return ResponseDto.success("success member delete!");
    }

    public ResponseDto<?> integrationMember(IntegrationMemberRequestDto requestDto, HttpServletResponse response) {
        Member member = memberRepository.findMemberByEmailAndOauth(requestDto.getEmail(), requestDto.getOauth());
        String oauth = member.getOauth();
        String new_oauth = oauth + "," + requestDto.getOauth();

        member.updateOauth(new_oauth);
        memberRepository.save(member);

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);

        response.addHeader("Authorization", tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());

        IntegrationResponseDto responseDto = new IntegrationResponseDto(member, tokenDto);

        return ResponseDto.success(responseDto);
    }

    public ResponseDto<?> getMemberProfile() {
        List<ProfileList> profileLists = profileListRepository.findAll();
        List<ProfileResponseDto> profileResponseDtos = new ArrayList<>();
        for(ProfileList profileList : profileLists){
            profileResponseDtos.add(new ProfileResponseDto(profileList));
        }
        return ResponseDto.success(profileResponseDtos);
    }
}
