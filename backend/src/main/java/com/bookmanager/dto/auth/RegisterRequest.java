package com.bookmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
@Schema(description = "Dados para criação de nova conta")
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Nome completo do usuário", example = "João Silva", minLength = 2, maxLength = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "E-mail do usuário (utilizado como login)", example = "joao@email.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 72, message = "Password must be between 6 and 72 characters")
    @Schema(description = "Senha da conta", example = "senha123", minLength = 6, maxLength = 72)
    private String password;
}
