package com.kvanzi.jwtauthorization.service.impl;

import com.kvanzi.jwtauthorization.dto.LoginRequest;
import com.kvanzi.jwtauthorization.dto.SignupRequest;
import com.kvanzi.jwtauthorization.entity.User;
import com.kvanzi.jwtauthorization.entity.UserRole;
import com.kvanzi.jwtauthorization.exception.UserAlreadyExistsException;
import com.kvanzi.jwtauthorization.exception.UserNotFoundException;
import com.kvanzi.jwtauthorization.repository.UserRepository;
import com.kvanzi.jwtauthorization.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public User create(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User with username '%s' already exists!".formatted(request.getUsername()));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ROLE_USER);

        return userRepository.save(user);
    }

    @Override
    public User authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username '%s' not found!".formatted(request.getUsername())));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        return user;
    }
}
