package com.bookmanager.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
@Schema(description = "Dados para criação de nova conta")
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva", minLength = 2, maxLength = 100)
    private String name;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Schema(description = "E-mail do usuário (utilizado como login)", example = "joao@email.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
    @Schema(description = "Senha da conta", example = "senha123", minLength = 6, maxLength = 72)
    private String password;
}
