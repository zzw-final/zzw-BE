package com.zzw.zzw_final.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zzw.zzw_final.Config.GoogleLoginConfiguration;
import com.zzw.zzw_final.Dto.Request.IntegrationMemberRequestDto;
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
    private final GoogleLoginConfiguration configUtils;
    private final GoogleService googleService;
    private final NaverService naverService;

    // 카카오 소셜로그인
    @GetMapping("/api/member/login/kakao")
    public ResponseDto<?> callBackKakao(@RequestParam(name = "code") String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
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

    //구글 소셜로그인
    @GetMapping(value = "/api/member/login/google")
    public ResponseDto<?> redirectGoogleLogin(@RequestParam(value = "code") String authCode,
                                              HttpServletResponse response) {
        return googleService.googleLogin(authCode, response);
    }

    //네이버 소셜로그인
    @GetMapping(value = "/api/member/login/naver")
    public ResponseDto<?> redirectNaverLogin(@RequestParam(value = "code") String authCode,
                                             HttpServletResponse response, @RequestParam(value = "state", required = false) String state) throws JsonProcessingException {
        return naverService.naverLogin(authCode, response, state);
    }

    @PutMapping("/api/member/integration")
    public ResponseDto<?> integrationMember(@RequestBody IntegrationMemberRequestDto requestDto, HttpServletResponse response){
        return memberService.integrationMember(requestDto, response);
    }

    @GetMapping("/api/member/profile")
    public ResponseDto<?> getMemberProfile(){
        return memberService.getMemberProfile();
    }

    @PutMapping("/api/member/profile/{profileId}")
    public ResponseDto<?> updateMemberProfile(HttpServletRequest request, @PathVariable Long profileId){
        return memberService.updateMemberProfile(request, profileId);
    }

    @GetMapping("/api/member/grade")
    public ResponseDto<?> getMemberGrade(HttpServletRequest request){
        return memberService.getMemberGrade(request);
    }

    @PutMapping("/api/member/grade/{gradeId}")
    public ResponseDto<?> updateMemberGrade(HttpServletRequest request, @PathVariable Long gradeId){
        return memberService.updateMemberGrade(request, gradeId);
    }
}
