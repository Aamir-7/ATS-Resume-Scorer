package com.ResumeScore.ATS.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequest {
    private String name;
    private String email;
    private String password;
}
