package com.zzw.zzw_final.Config;

import com.zzw.zzw_final.Config.Jwt.JwtFilter;
import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration  extends WebSecurityConfigurerAdapter{

    private final TokenProvider tokenProvider;

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();

        http
                .addFilterBefore(new JwtFilter(tokenProvider, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/**").authenticated()
                .antMatchers("/zzw/**").permitAll()
                .anyRequest().permitAll();
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/api/member/**", "/api/post/**","/api/chat/**", "/zzw/**");
    }

}
