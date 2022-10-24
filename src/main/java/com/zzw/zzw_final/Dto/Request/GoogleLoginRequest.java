package com.zzw.zzw_final.Dto.Request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleLoginRequest {

    private String clientId;
    private String redirectUri;
    private String clientSecret;
    private String responseType;
    private String scope;
    private String code;
    private String accessType;
    private String grantType;
    private String state;
    private String includeGrantedScopes;
    private String loginHint;
    private String prompt;
}