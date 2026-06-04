package com.bookmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
@Schema(description = "Credenciais para autenticação")
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "E-mail cadastrado", example = "joao@email.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(max = 72, message = "Password must be at most 72 characters")
    @Schema(description = "Senha da conta", example = "senha123", maxLength = 72)
    private String password;
}
