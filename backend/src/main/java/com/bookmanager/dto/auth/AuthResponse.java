package com.bookmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com token JWT e dados do usuário")
public record AuthResponse(
    @Schema(description = "Token JWT para uso no header Authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
    String token,

    @Schema(description = "Nome do usuário autenticado", example = "João Silva")
    String name,

    @Schema(description = "E-mail do usuário autenticado", example = "joao@email.com")
    String email
) {}
