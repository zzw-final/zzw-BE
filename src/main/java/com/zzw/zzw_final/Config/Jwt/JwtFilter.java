package com.zzw.zzw_final.Config.Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private final TokenProvider tokenProvider;
    @Autowired
    private final UserDetailsService userDetailsService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        String token = request.getHeader("Authorization").substring(7);
        String oauth = request.getHeader("oauth");

        if (token != null ) {
            String email = tokenProvider.getUserEmail(token);

            if(email != null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email+","+oauth);

                if (tokenProvider.validateToken(token)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken  = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

        }
        filterChain.doFilter(request, response);
    }
}
