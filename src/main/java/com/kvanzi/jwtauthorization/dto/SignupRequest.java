package com.kvanzi.jwtauthorization.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SignupRequest {

    @NotEmpty(message = "Username can't be empty.")
    private String username;

    @NotEmpty(message = "Password can't be empty.")
    private String password;
}
