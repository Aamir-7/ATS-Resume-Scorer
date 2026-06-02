package com.ResumeScore.ATS.auth;

import com.ResumeScore.ATS.auth.dto.AuthResponse;
import com.ResumeScore.ATS.auth.dto.LoginRequest;
import com.ResumeScore.ATS.auth.dto.SignupRequest;
import com.ResumeScore.ATS.user.User;
import com.ResumeScore.ATS.user.UserRepository;
import com.ResumeScore.ATS.user.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse signIn(LoginRequest request) {

        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new IllegalArgumentException("Invalid email or password "));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
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
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(UserRole.USER);

        User savedUser=userRepository.save(newUser);
        String token= jwtService.generateToken(savedUser.getId(), savedUser.getRole());
        return new AuthResponse(
                token,
                newUser.getEmail(),
                "Sign up successful"
        );
    }
}
