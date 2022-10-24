package com.zzw.zzw_final.Config.Jwt;

import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.RefreshToken;
import com.zzw.zzw_final.Dto.TokenDto;
import com.zzw.zzw_final.Repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class TokenProvider {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7 ;
    private static final long REFRESH_TOKEN_EXPRIRE_TIME = 1000 * 60 * 60 * 24 * 7;

    private final Key key;
    private final RefreshTokenRepository refreshTokenRepository;


    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Member member) {
        long now = (new Date().getTime());

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(member.getEmail())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPRIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(member.getId())
                .member(member)
                .value(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenObject);

        return TokenDto.builder()
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();

    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    @Transactional
    public String getUserEmail(String token) {
        return this.parseClaims(token).getSubject();
    }

    @Transactional
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}
