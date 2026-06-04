package com.bookmanager.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book Manager API")
                        .description("""
                                API REST para gerenciamento pessoal de livros com autenticação JWT.
                                
                                ## Autenticação
                                Utilize `/auth/register` ou `/auth/login` para obter um token JWT.
                                O token é retornado no body da resposta e também definido automaticamente \
                                como cookie `HttpOnly`.
                                Para endpoints protegidos, clique em **Authorize** e insira: `Bearer <token>`.
                                
                                ## Isolamento de dados
                                Cada usuário visualiza e gerencia apenas os seus próprios livros.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Book Manager")
                                .email("contato@bookmanager.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT obtido via `/auth/login` ou `/auth/register`")));
    }
}
