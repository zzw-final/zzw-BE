package com.zzw.zzw_final.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GoogleLoginConfiguration {
    @Value("${google.auth.url}")
    private String googleAuthUrl;

    @Value("${google.redirect.uri}")
    private String googleRedirectUrl;

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.secret}")
    private String googleSecret;

    public String getGoogleAuthUrl() {
        return googleAuthUrl;
    }


    public String getGoogleClientId() {
        return googleClientId;
    }

    public String getGoogleRedirectUri() {
        return googleRedirectUrl;
    }

    public String getGoogleSecret() {
        return googleSecret;
    }

}