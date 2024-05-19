package com.GreenThumb.GT.services;

import com.GreenThumb.GT.models.User.User;
import com.GreenThumb.GT.payload.authentication.AuthenticationPayload;
import com.GreenThumb.GT.payload.authentication.RegisterPayload;
import com.GreenThumb.GT.repositories.UserRepository;
import com.GreenThumb.GT.response.AuthenticationResponse;
import com.GreenThumb.GT.security.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;
private final AuthenticationManager authenticationManager;
    public com.GreenThumb.GT.response.AuthenticationResponse register(RegisterPayload request) {


        var user= User.builder()
                .userName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(request.getRole())
                .build();
        userRepository.save(user);

        var jwtToken= jwtService.generateToken(user);
        return com.GreenThumb.GT.response.AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationPayload request) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken= jwtService.generateToken(user);
        return com.GreenThumb.GT.response.AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }
}
