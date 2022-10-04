package com.zzw.zzw_final.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.OauthUserDto;
import com.zzw.zzw_final.Dto.Response.OAuthResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverService {

    @Value("${naver.client.id}")
    private String NaverClientId;
    @Value("${naver.client.secret}")
    private String NaverClientSecret;

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public ResponseDto<?> naverLogin(String code, HttpServletResponse response, String state) throws JsonProcessingException {

        String accessToken = getAccessToken(code, state);

        // 2. 토큰으로 네이버 API 호출
        OauthUserDto naverUserDto = getNaverUserInfo(accessToken);

        // 3. DB에 등록된 유저인지 판별
        List<Member> members = memberRepository.findAllByEmail(naverUserDto.getEmail());

        if (members.size() == 0){
            OAuthResponseDto responseDto = new OAuthResponseDto(naverUserDto.getEmail(), accessToken, "naver", false);
            return ResponseDto.success(responseDto);
        }else{
            for(Member member : members){
                String oauth = member.getOauth();
                if (oauth.contains("naver")){
                    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
                    OAuthResponseDto responseDto = new OAuthResponseDto(member, tokenDto, accessToken, "naver");
                    response.addHeader("Authorization", tokenDto.getAccessToken());
                    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
                    return ResponseDto.success(responseDto);
                }
            }
            OAuthResponseDto responseDto = new OAuthResponseDto(naverUserDto.getEmail(), accessToken, "naver", true);
            return ResponseDto.success(responseDto);
        }
    }

    public String getAccessToken(String code, String state) throws JsonProcessingException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("client_id", NaverClientId);
        params.add("client_secret", NaverClientSecret);
        params.add("grant_type", "authorization_code");
        params.add("state", state);  // state 일치를 확인
        params.add("code", code);

        HttpEntity<MultiValueMap<String,String>> naverTokenRequest = new HttpEntity<>(params,httpHeaders);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private OauthUserDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class
        );

        // HTTP 응답 받아오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("response").get("id").asLong();
        String email = jsonNode.get("response").get("email").asText();

        return new OauthUserDto(id, email);
    }

    private Member registerNaverUserIfNeeded(OauthUserDto naverDto) {
        Member naverUser = new Member(naverDto);
        memberRepository.save(naverUser);
        forceLogin(naverUser);
        return naverUser;
    }

    private void forceLogin(Member naverUser) {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setAccount(naverUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
