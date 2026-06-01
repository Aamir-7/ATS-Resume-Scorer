package com.ResumeScore.ATS.auth;

import com.ResumeScore.ATS.auth.dto.AuthResponse;
import com.ResumeScore.ATS.auth.dto.LoginRequest;
import com.ResumeScore.ATS.auth.dto.SignupRequest;
import com.ResumeScore.ATS.user.User;
import com.ResumeScore.ATS.user.UserRepository;
import com.ResumeScore.ATS.user.UserRole;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public AuthResponse signIn(LoginRequest request) {

        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new IllegalArgumentException("Invalid email or password "));

        if (!user.getPassword().equals(request.getPassword())){
            throw new IllegalArgumentException("Invalid email or password ");
        }

        String token= jwtService.generateToken(user.getId(), user.getRole());
        return new AuthResponse(
                token,
                user.getEmail(),
                "Sign in successful"
        );
    }

    public AuthResponse signUp(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalArgumentException("User already exists ");
        }

        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setRole(UserRole.USER);

        User savedUser=userRepository.save(newUser);
        String token= jwtService.generateToken(newUser.getId(), newUser.getRole());
        return new AuthResponse(
                token,
                newUser.getEmail(),
                "Sign up successful"
        );
    }
}
