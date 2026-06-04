package com.bookmanager.service;

import com.bookmanager.dto.auth.AuthResponse;
import com.bookmanager.dto.auth.LoginRequest;
import com.bookmanager.dto.auth.RegisterRequest;
import com.bookmanager.entity.User;
import com.bookmanager.exception.EmailAlreadyInUseException;
import com.bookmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks AuthService authService;

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setName("João");
        request.setEmail("joao@email.com");
        request.setPassword("senha123");

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("token123");

        AuthResponse result = authService.register(request);

        assertThat(result.token()).isEqualTo("token123");
        assertThat(result.email()).isEqualTo("joao@email.com");
        assertThat(result.name()).isEqualTo("João");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailAlreadyInUse_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setName("João");
        request.setEmail("joao@email.com");
        request.setPassword("senha123");

        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyInUseException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("joao@email.com");
        request.setPassword("senha123");

        User user = User.builder().id(1L).name("João").email("joao@email.com").password("hashed").build();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtService.generateToken(user)).thenReturn("token123");

        AuthResponse result = authService.login(request);

        assertThat(result.token()).isEqualTo("token123");
        assertThat(result.email()).isEqualTo("joao@email.com");
    }

    @Test
    void login_badCredentials_throwsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("joao@email.com");
        request.setPassword("errada");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
