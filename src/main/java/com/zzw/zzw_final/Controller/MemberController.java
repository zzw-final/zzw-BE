package com.zzw.zzw_final.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zzw.zzw_final.Config.GoogleLoginConfiguration;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.GoogleService;
import com.zzw.zzw_final.Service.KakaoService;
import com.zzw.zzw_final.Service.MemberService;
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
    private final GoogleLoginConfiguration configUtils;
    private final GoogleService googleService;

    // 카카오 소셜로그인
    @GetMapping("/api/member/login/kakao")
    public ResponseDto<?> callBackKakao(@RequestParam(name = "code") String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    @PutMapping("/api/auth/member/signup")
    public ResponseDto<?> postUserNickname(HttpServletRequest request, @RequestBody SignupRequestDto requestDto){
        return memberService.postUserNickname(request, requestDto);
    }

    @GetMapping("/api/member/kakao/logout")
    public ResponseDto<?> kakaoLogout(HttpServletRequest request){
        return kakaoService.logout(request);
    }

    @GetMapping(value = "/google/login")
    public ResponseEntity<Object> moveGoogleInitUrl() {
        String authUrl = configUtils.googleInitUrl();
        URI redirectUri = null;
        try {
            redirectUri = new URI(authUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/api/member/login/google")
    public ResponseDto<?> redirectGoogleLogin(@RequestParam(value = "code") String authCode,
                                              HttpServletResponse response) {
        return googleService.googleLogin(authCode, response);
    }
}
