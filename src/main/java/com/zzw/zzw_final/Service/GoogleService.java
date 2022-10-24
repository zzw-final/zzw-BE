package com.zzw.zzw_final.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.zzw.zzw_final.Config.GoogleLoginConfiguration;
import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.GoogleLoginDto;
import com.zzw.zzw_final.Dto.Request.GoogleLoginRequest;
import com.zzw.zzw_final.Dto.Response.GoogleLoginResponse;
import com.zzw.zzw_final.Dto.Response.OAuthResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GoogleService {

    private final GoogleLoginConfiguration googleLoginConfiguration;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final UserDetailsService details;

    public ResponseDto<?> googleLogin(String authCode, HttpServletResponse response) {

        //authCode를 가지고 구글 유저 가져오기
        GoogleLoginDto googleUser = FindGoogleUser(authCode);
        List<Member> members = memberRepository.findAllByEmail(googleUser.getEmail());

        if (members.size() == 0){
            OAuthResponseDto responseDto = new OAuthResponseDto(googleUser.getEmail(), "googleToken", "google", false);
            return ResponseDto.success(responseDto);
        }else{
            for(Member member : members){
                String oauth = member.getOauth();
                if (oauth.contains("google")){
                    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
                    response.addHeader("Authorization", tokenDto.getAccessToken());
                    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
                    forceLogin(member);
                    OAuthResponseDto responseDto = new OAuthResponseDto(member, tokenDto, "googleToken", "google", memberService.getInvalidToken());
                    return ResponseDto.success(responseDto);
                }
            }
            OAuthResponseDto responseDto = new OAuthResponseDto(googleUser.getEmail(), "googleToken", "google", true);
            return ResponseDto.success(responseDto);
        }
    }

    public GoogleLoginDto FindGoogleUser(String code){
        // HTTP 통신을 위해 RestTemplate 활용
        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(googleLoginConfiguration.getGoogleClientId())
                .clientSecret(googleLoginConfiguration.getGoogleSecret())
                .code(code)
                .redirectUri(googleLoginConfiguration.getGoogleRedirectUri())
                .grantType("authorization_code")
                .build();

        try {
            // Http Header 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
            ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(googleLoginConfiguration.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);

            // ObjectMapper를 통해 String to Object로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
            GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

            // 사용자의 정보는 JWT Token으로 저장되어 있고, Id_Token에 값을 저장한다.
            String jwtToken = googleLoginResponse.getIdToken();

            // JWT Token을 전달해 JWT 저장된 사용자 정보 확인
            String requestUrl = UriComponentsBuilder.fromHttpUrl(googleLoginConfiguration.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();

            String resultJson = restTemplate.getForObject(requestUrl, String.class);

            if(resultJson != null) {
                GoogleLoginDto userInfoDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {});

                return userInfoDto;
            }
            else {
                throw new Exception("Google OAuth failed!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void forceLogin(Member googleUser) {
        UserDetails userDetails = this.details.loadUserByUsername(googleUser.getEmail()+","+googleUser.getOauth());
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
