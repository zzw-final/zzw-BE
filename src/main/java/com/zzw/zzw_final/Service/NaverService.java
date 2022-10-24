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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
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
    private final MemberService memberService;
    private final UserDetailsService details;

    public ResponseDto<?> naverLogin(String code, HttpServletResponse response, String state) throws JsonProcessingException {

        String accessToken = getAccessToken(code, state);

        OauthUserDto naverUserDto = getNaverUserInfo(accessToken);

        List<Member> members = memberRepository.findAllByEmail(naverUserDto.getEmail());

        if (members.size() == 0){
            OAuthResponseDto responseDto = new OAuthResponseDto(naverUserDto.getEmail(), accessToken, "naver", false);
            return ResponseDto.success(responseDto);
        }else{
            for(Member member : members){
                String oauth = member.getOauth();
                if (oauth.contains("naver")){
                    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
                    OAuthResponseDto responseDto = new OAuthResponseDto(member, tokenDto, accessToken, "naver", memberService.getInvalidToken());
                    response.addHeader("Authorization", tokenDto.getAccessToken());
                    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
                    forceLogin(member);
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
        params.add("state", state);
        params.add("code", code);

        HttpEntity<MultiValueMap<String,String>> naverTokenRequest = new HttpEntity<>(params,httpHeaders);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    private OauthUserDto getNaverUserInfo(String accessToken) throws JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("response").get("id").asLong();
        String email = jsonNode.get("response").get("email").asText();

        return new OauthUserDto(id, email);
    }

    private void forceLogin(Member naverUser) {
        UserDetails userDetails = this.details.loadUserByUsername(naverUser.getEmail()+","+naverUser.getOauth());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
