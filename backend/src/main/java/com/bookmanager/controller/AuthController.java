package com.bookmanager.controller;

import com.bookmanager.dto.auth.*;
import com.bookmanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registro, login e logout de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria uma nova conta de usuário. Retorna o token JWT no body e define o cookie `token` (HttpOnly) automaticamente."
    )
    @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
        content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (campo obrigatório ausente ou formato incorreto)",
        content = @Content)
    @ApiResponse(responseCode = "409", description = "E-mail já cadastrado",
        content = @Content)
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest request,
                                                 HttpServletResponse response) {
        AuthResponse auth = authService.register(request);
        addTokenCookie(response, auth.token());
        return ResponseEntity.status(HttpStatus.CREATED).body(auth);
    }

    @PostMapping("/login")
    @Operation(
        summary = "Autenticar usuário",
        description = "Realiza o login com e-mail e senha. Retorna o token JWT no body e define o cookie `token` (HttpOnly) com validade de 24 horas."
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
        content = @Content(schema = @Schema(implementation = AuthResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
        content = @Content)
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas (e-mail ou senha incorretos)",
        content = @Content)
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request,
                                              HttpServletResponse response) {
        AuthResponse auth = authService.login(request);
        addTokenCookie(response, auth.token());
        return ResponseEntity.ok(auth);
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Encerrar sessão",
        description = "Invalida a sessão do usuário limpando o cookie `token` (define `Max-Age=0`). Não requer body."
    )
    @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso", content = @Content)
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }

    private void addTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .path("/")
                .maxAge(86400)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
