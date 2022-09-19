package com.zzw.zzw_final.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.KakaoService;
import com.zzw.zzw_final.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final KakaoService kakaoService;
    private final MemberService memberService;

    @GetMapping("/api/member/login/kakao")
    public ResponseDto<?> callBackKakao(@RequestParam(name = "code") String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    @PutMapping("/api/auth/member/signup")
    public ResponseDto<?> postUserNickname(HttpServletRequest request, @RequestBody SignupRequestDto requestDto){
        return memberService.postUserNickname(request, requestDto);
    }
}
