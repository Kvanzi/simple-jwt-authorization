package com.kvanzi.jwtauthorization.fitler;

import com.kvanzi.jwtauthorization.security.JwtUserDetails;
import com.kvanzi.jwtauthorization.service.JwtService;
import com.kvanzi.jwtauthorization.service.JwtUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtUserDetailsService jwtUserDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, JwtUserDetailsService jwtUserDetailsService) {
        this.jwtService = jwtService;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String accessJwt = getValueFromCookies(request, "access");
        final String refreshJwt = getValueFromCookies(request, "refresh");
        long userId;

        if (accessJwt != null && jwtService.isSupportedJwt(accessJwt)) {
            userId = jwtService.extractUserId(accessJwt);
        } else if (refreshJwt != null && jwtService.isSupportedJwt(refreshJwt)) {
            userId = jwtService.extractUserId(refreshJwt);
        } else {
            filterChain.doFilter(request, response);
            return;
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (userId != 0 && authentication == null) {
            JwtUserDetails jwtUserDetails = jwtUserDetailsService.loadUserById(userId);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    jwtUserDetails,
                    null,
                    jwtUserDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            if (accessJwt != null && jwtService.isTokenValid(accessJwt, jwtUserDetails)) {
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("Authenticated user '%s' for %s.".formatted(jwtUserDetails.getUsername(), request.getRequestURI()));
            } else if (refreshJwt != null && jwtService.isTokenValid(refreshJwt, jwtUserDetails)) {
                response.addCookie(buildJwtAccessCookie(userId));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("Access token refreshed for user '%s'.".formatted(jwtUserDetails.getUsername()));
                log.info("Authenticated user '%s' for %s.".formatted(jwtUserDetails.getUsername(), request.getRequestURI()));
            }
        }

        filterChain.doFilter(request, response);
    }

    private Cookie buildJwtAccessCookie(long userId) {
        Cookie accessCookie = new Cookie("access", jwtService.buildAccessToken(userId));
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(jwtService.getJwtAccessExpirationMills());
        accessCookie.setSecure(true);

        return accessCookie;
    }

    private String getValueFromCookies(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
