package com.kvanzi.jwtauthorization.service;

import com.kvanzi.jwtauthorization.security.JwtUserDetails;

public interface JwtService {
    String buildAccessToken(long userId);
    String buildRefreshToken(long userId);
    long extractUserId(String token);
    boolean isTokenValid(String token, JwtUserDetails JwtUserDetails);
    int getJwtRefreshExpirationMills();
    int getJwtAccessExpirationMills();
    boolean isSupportedJwt(String token);
}
