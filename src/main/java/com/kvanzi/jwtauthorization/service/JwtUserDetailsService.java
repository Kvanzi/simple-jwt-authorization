package com.kvanzi.jwtauthorization.service;

import com.kvanzi.jwtauthorization.exception.UserNotFoundException;
import com.kvanzi.jwtauthorization.security.JwtUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface JwtUserDetailsService extends UserDetailsService {
    JwtUserDetails loadUserById(long id) throws UserNotFoundException;
}
