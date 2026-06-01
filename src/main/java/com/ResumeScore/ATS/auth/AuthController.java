package com.ResumeScore.ATS.auth;

import com.ResumeScore.ATS.auth.dto.AuthResponse;
import com.ResumeScore.ATS.auth.dto.LoginRequest;
import com.ResumeScore.ATS.auth.dto.SignupRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public AuthResponse signUp(
            @RequestBody SignupRequest request
    ){
        return authService.signUp(request);
    }

    @PostMapping("/signin")
    public AuthResponse signIn(
            @RequestBody LoginRequest request
            ){
        return authService.signIn(request);
    }
}
