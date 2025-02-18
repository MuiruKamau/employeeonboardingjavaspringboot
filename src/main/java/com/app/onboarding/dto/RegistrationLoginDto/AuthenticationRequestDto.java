package com.app.onboarding.dto.RegistrationLoginDto;



import lombok.Data;

@Data
public class AuthenticationRequestDto {
    private String username;
    private String password;
}