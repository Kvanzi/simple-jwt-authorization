package com.kvanzi.jwtauthorization.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtUserDetails extends UserDetails {
    long getId();
}
