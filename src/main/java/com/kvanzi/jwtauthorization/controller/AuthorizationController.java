package com.kvanzi.jwtauthorization.controller;

import com.kvanzi.jwtauthorization.dto.LoginRequest;
import com.kvanzi.jwtauthorization.dto.SignupRequest;
import com.kvanzi.jwtauthorization.entity.User;
import com.kvanzi.jwtauthorization.service.JwtService;
import com.kvanzi.jwtauthorization.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthorizationController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthorizationController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            final @RequestBody SignupRequest signupRequest
    ) {
        User user = userService.create(signupRequest);
        log.info("Created new user: " + user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            final @RequestBody LoginRequest loginRequest,
            final HttpServletResponse response
    ) {
        User user = userService.authenticate(loginRequest);
        String jwtAccess = jwtService.buildAccessToken(user.getId());
        String jwtRefresh = jwtService.buildRefreshToken(user.getId());

        Cookie jwtAccessCookie = new Cookie("access", jwtAccess);
        jwtAccessCookie.setHttpOnly(true);
        jwtAccessCookie.setPath("/");
        jwtAccessCookie.setMaxAge(jwtService.getJwtAccessExpirationMills() / 1000);
        jwtAccessCookie.setSecure(true);

        Cookie jwtRefreshCookie = new Cookie("refresh", jwtRefresh);
        jwtRefreshCookie.setHttpOnly(true);
        jwtRefreshCookie.setPath("/");
        jwtRefreshCookie.setMaxAge(jwtService.getJwtRefreshExpirationMills() / 1000);
        jwtRefreshCookie.setSecure(true);

        response.addCookie(jwtAccessCookie);
        response.addCookie(jwtRefreshCookie);

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
        ));
    }
}
