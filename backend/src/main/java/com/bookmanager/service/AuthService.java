package com.bookmanager.service;

import com.bookmanager.dto.auth.*;
import com.bookmanager.entity.User;
import com.bookmanager.repository.UserRepository;
import com.bookmanager.exception.EmailAlreadyInUseException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException();
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user), user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        if (!(auth.getPrincipal() instanceof User user)) {
            throw new AuthenticationServiceException("Unexpected principal type");
        }
        return new AuthResponse(jwtService.generateToken(user), user.getName(), user.getEmail());
    }

}
