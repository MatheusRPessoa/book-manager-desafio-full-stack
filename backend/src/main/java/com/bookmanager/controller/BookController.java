package com.bookmanager.controller;

import com.bookmanager.dto.book.*;
import com.bookmanager.entity.User;
import com.bookmanager.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Gerenciamento de livros do usuário autenticado")
@SecurityRequirement(name = "bearerAuth")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(
        summary = "Listar livros",
        description = "Retorna uma página de livros do usuário autenticado. Suporta filtragem por título, autor e intervalo de ano de publicação. " +
                      "Paginação controlada pelos parâmetros `page`, `size` e `sort` (ex: `sort=title,asc`)."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content)
    public ResponseEntity<Page<BookResponse>> list(
            @Parameter(description = "Filtrar por trecho do título (case-insensitive)")
            @RequestParam(required = false) String title,

            @Parameter(description = "Filtrar por trecho do nome do autor (case-insensitive)")
            @RequestParam(required = false) String author,

            @Parameter(description = "Ano de publicação mínimo (inclusive)")
            @RequestParam(required = false) Integer yearFrom,

            @Parameter(description = "Ano de publicação máximo (inclusive)")
            @RequestParam(required = false) Integer yearTo,

            @Parameter(description = "Paginação: `page` (0-based), `size`, `sort` (ex: `title,asc`)")
            Pageable pageable,

            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookService.listBooks(title, author, yearFrom, yearTo, user, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar livro por ID",
        description = "Retorna os detalhes de um livro específico. O livro deve pertencer ao usuário autenticado."
    )
    @ApiResponse(responseCode = "200", description = "Livro encontrado",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content)
    @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = @Content)
    public ResponseEntity<BookResponse> getById(
            @Parameter(description = "ID do livro", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookService.findById(id, user));
    }

    @PostMapping
    @Operation(
        summary = "Cadastrar livro",
        description = "Cria um novo livro vinculado ao usuário autenticado."
    )
    @ApiResponse(responseCode = "201", description = "Livro criado com sucesso",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content)
    public ResponseEntity<BookResponse> create(
            @RequestBody @Valid BookRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(request, user));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar livro",
        description = "Atualiza todos os campos de um livro existente. O livro deve pertencer ao usuário autenticado."
    )
    @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso",
        content = @Content(schema = @Schema(implementation = BookResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content)
    @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = @Content)
    public ResponseEntity<BookResponse> update(
            @Parameter(description = "ID do livro", required = true)
            @PathVariable Long id,
            @RequestBody @Valid BookRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(bookService.update(id, request, user));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir livro",
        description = "Remove permanentemente um livro. O livro deve pertencer ao usuário autenticado."
    )
    @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente ou inválido", content = @Content)
    @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = @Content)
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do livro", required = true)
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        bookService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
