package com.app.onboarding.dto.RegistrationLoginDto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;
}