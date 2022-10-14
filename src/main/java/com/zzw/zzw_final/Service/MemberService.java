package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.GradeListResponseDto;
import com.zzw.zzw_final.Dto.Response.IntegrationResponseDto;
import com.zzw.zzw_final.Dto.Response.MypageUserInfoResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.zzw.zzw_final.Dto.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final FollowRepository followRepository;
    private final GradeRepository gradeRepository;
    private final GradeListRepository gradeListRepository;

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
        IntegrationResponseDto responseDto = new IntegrationResponseDto(member, tokenDto, getInvalidToken());
        GradeList gradeList = gradeListRepository.findGradeListById(3006L);
        Grade grade = new Grade(member, gradeList);
        gradeRepository.save(grade);

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


    public String getInvalidToken(){
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DATE, 1);
        Date date = new Date(cal1.getTimeInMillis());
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return transFormat.format(date);
    }

    public ResponseDto<?> getUserInfo(HttpServletRequest request) {

        ResponseDto<?> result = checkMember(request);
        Member member = (Member) result.getData();

        List<Follow> followerlist = followRepository.findAllByFollowerId(member.getId());
        List<Follow> followList = followRepository.findAllByMember(member);

        List<GradeListResponseDto> responseDtos = getUserGrade(member);

        MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                followerlist.size(), followList.size(), responseDtos, true);

        return ResponseDto.success(responseDto);
    }



    public ResponseDto<?> getOtherUserInfo(Long user_id, HttpServletRequest request) {

        Member loginMember = getMember(request);
        Member member = memberRepository.findMemberById(user_id);

        List<Follow> followerlist = followRepository.findAllByFollowerId(member.getId());
        List<Follow> followList = followRepository.findAllByMember(member);

        List<GradeListResponseDto> responseDtos = getUserGrade(member);


        MypageUserInfoResponseDto responseDto;

        if(loginMember == null){
            responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos, false);

            return ResponseDto.success(responseDto);
        }
        else
            responseDto = getUserInfoResponseDto(loginMember, member, followerlist, followList, responseDtos);

        return ResponseDto.success(responseDto);
    }

    private MypageUserInfoResponseDto getUserInfoResponseDto(Member loginMember, Member member, List<Follow> followerlist,
                                                             List<Follow> followList, List<GradeListResponseDto> responseDtos) {
        Follow follow = followRepository.findFollowByFollowerIdAndMember(loginMember.getId(), member);

        if (follow == null) {
            MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos,false);
            return responseDto;

        } else {
            MypageUserInfoResponseDto responseDto = new MypageUserInfoResponseDto(member,
                    followerlist.size(), followList.size(), responseDtos,true);
            return responseDto;
        }
    }

    public List<GradeListResponseDto> getUserGrade(Member member) {
        List<GradeListResponseDto> gradeListResponseDtos = new ArrayList<>();

        List<Grade> grades = gradeRepository.findAllByMember(member);
        for(Grade grade : grades){
            gradeListResponseDtos.add(new GradeListResponseDto(grade.getGradeList()));
        }
        return gradeListResponseDtos;
    }
}
