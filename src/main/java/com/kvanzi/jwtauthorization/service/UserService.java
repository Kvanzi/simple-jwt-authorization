package com.kvanzi.jwtauthorization.service;

import com.kvanzi.jwtauthorization.dto.LoginRequest;
import com.kvanzi.jwtauthorization.dto.SignupRequest;
import com.kvanzi.jwtauthorization.entity.User;

public interface UserService {
    User create(SignupRequest request);
    User authenticate(LoginRequest request);
}
