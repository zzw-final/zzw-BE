package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.zzw.zzw_final.Dto.ErrorCode.INVALID_TOKEN;
import static com.zzw.zzw_final.Dto.ErrorCode.NULL_TOKEN;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

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

        //헤더에 정상적으로 토큰이 전달 됐을 경우
        Member member = tokenProvider.getMemberFromAuthentication();

        return ResponseDto.success(member);
    }

    public ResponseDto<?> postUserNickname(HttpServletRequest request, SignupRequestDto requestDto) {

        //로그인 토큰 유효성 검증하기
        checkMember(request);
        String email = tokenProvider.getUserEmail(request.getHeader("Authorization").substring(7));

        Member member = memberRepository.findMemberByEmail(email);
        member.update(requestDto.getNickname());

        memberRepository.save(member);

        return ResponseDto.success("success signup");
    }
}
