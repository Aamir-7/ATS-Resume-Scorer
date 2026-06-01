package com.ResumeScore.ATS.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String message;
}
