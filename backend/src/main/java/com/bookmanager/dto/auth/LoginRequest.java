package com.bookmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
@Schema(description = "Credenciais para autenticação")
public class LoginRequest {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Schema(description = "E-mail cadastrado", example = "joao@email.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(max = 72, message = "A senha deve ter no máximo 72 caracteres")
    @Schema(description = "Senha da conta", example = "senha123", maxLength = 72)
    private String password;
}
