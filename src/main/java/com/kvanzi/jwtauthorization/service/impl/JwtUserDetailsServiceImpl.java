package com.kvanzi.jwtauthorization.service.impl;

import com.kvanzi.jwtauthorization.exception.UserNotFoundException;
import com.kvanzi.jwtauthorization.repository.UserRepository;
import com.kvanzi.jwtauthorization.security.JwtUserDetails;
import com.kvanzi.jwtauthorization.service.JwtUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceImpl implements JwtUserDetailsService {
    private final UserRepository userRepository;

    public JwtUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public JwtUserDetails loadUserById(long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id '%d' not found!".formatted(id)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User '%s' not found!".formatted(username)));
    }
}
