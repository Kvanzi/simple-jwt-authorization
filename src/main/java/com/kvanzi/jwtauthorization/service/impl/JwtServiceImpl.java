package com.kvanzi.jwtauthorization.service.impl;

import com.kvanzi.jwtauthorization.security.JwtUserDetails;
import com.kvanzi.jwtauthorization.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private final int JWT_ACCESS_EXPIRATION_MILLS;
    private final int JWT_REFRESH_EXPIRATION_MILLS;
    private final String JWT_SECRET_KEY;

    public JwtServiceImpl(
            @Value("${jwt.access-expiration}")
            int JWT_ACCESS_EXPIRATION_MILLS,

            @Value("${jwt.refresh-expiration}")
            int JWT_REFRESH_EXPIRATION_MILLS,

            @Value("${jwt.secret-key}")
            String JWT_SECRET_KEY
    ) {
        this.JWT_ACCESS_EXPIRATION_MILLS = JWT_ACCESS_EXPIRATION_MILLS;
        this.JWT_REFRESH_EXPIRATION_MILLS = JWT_REFRESH_EXPIRATION_MILLS;
        this.JWT_SECRET_KEY = JWT_SECRET_KEY;
    }

    @Override
    public String buildAccessToken(long userId) {
        return buildToken(
                new HashMap<>(),
                userId,
                JWT_ACCESS_EXPIRATION_MILLS
        );
    }

    @Override
    public String buildRefreshToken(long userId) {
        return buildToken(
                new HashMap<>(),
                userId,
                JWT_REFRESH_EXPIRATION_MILLS
        );
    }

    @Override
    public long extractUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    @Override
    public boolean isTokenValid(String token, JwtUserDetails jwtUserDetails) {
        final long extractedUserId = extractUserId(token);
        return !isTokenExpired(token) && (jwtUserDetails.getId() == extractedUserId);
    }

    @Override
    public boolean isSupportedJwt(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return e.getClaims() != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getJwtAccessExpirationMills() {
        return JWT_ACCESS_EXPIRATION_MILLS;
    }

    @Override
    public int getJwtRefreshExpirationMills() {
        return JWT_REFRESH_EXPIRATION_MILLS;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            long userId,
            long jwtExpirationTime
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return extractExpirationTime(token).before(new Date());
    }

    private Date extractExpirationTime(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        if (!isSupportedJwt(token)) {
            throw new UnsupportedJwtException("Unsupported Jwt!");
        }

        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    }
}
