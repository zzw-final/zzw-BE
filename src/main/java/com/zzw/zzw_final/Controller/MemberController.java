package com.zzw.zzw_final.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zzw.zzw_final.Dto.Request.NicknameUpdateRequestDto;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.GoogleService;
import com.zzw.zzw_final.Service.KakaoService;
import com.zzw.zzw_final.Service.MemberService;
import com.zzw.zzw_final.Service.NaverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final GoogleService googleService;
    private final NaverService naverService;

    @GetMapping("/api/member/login/{oauth}")
    public ResponseDto<?> callBackKakao(@PathVariable String oauth, @RequestParam(name = "code") String code,
                                        @RequestParam(value = "state", required = false) String state, HttpServletResponse response) throws JsonProcessingException {
        if (oauth.equals("kakao"))
            return kakaoService.kakaoLogin(code, response);
        else if (oauth.equals("google"))
            return googleService.googleLogin(code, response);
        else
            return naverService.naverLogin(code, response, state);
    }

    @PostMapping("/api/member/signup")
    public ResponseDto<?> postUserNickname(HttpServletResponse response, @RequestBody SignupRequestDto requestDto){
        return memberService.postUserNickname(response, requestDto);
    }

    @DeleteMapping("/api/member/resign/{member_id}")
    public ResponseDto<?> resignMember(@PathVariable Long member_id){
        return memberService.resignMember(member_id);
    }

    @GetMapping("/api/member/kakao/logout")
    public ResponseDto<?> kakaoLogout(HttpServletRequest request){
        return kakaoService.logout(request);
    }

    @GetMapping("/api/auth/mypage")
    public ResponseDto<?> getUserInfo(HttpServletRequest request){
        return memberService.getUserInfo(request);
    }

    @GetMapping("/api/mypage/{user_id}")
    public ResponseDto<?> getOtherUserInfo(@PathVariable Long user_id, HttpServletRequest request){
        return memberService.getOtherUserInfo(user_id, request);
    }

    @PutMapping("/api/auth/mypage/nickname")
    public ResponseDto<?> putUserNickname(HttpServletRequest request, @RequestBody NicknameUpdateRequestDto requestDto){
        return memberService.putUserNickname(request, requestDto);
    }
}

