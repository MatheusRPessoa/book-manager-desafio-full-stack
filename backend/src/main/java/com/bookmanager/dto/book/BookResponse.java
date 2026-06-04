package com.bookmanager.dto.book;

import com.bookmanager.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados de um livro retornado pela API")
public record BookResponse(
    @Schema(description = "ID único do livro", example = "1")
    Long id,

    @Schema(description = "Título do livro", example = "Clean Code")
    String title,

    @Schema(description = "Nome do autor", example = "Robert C. Martin")
    String author,

    @Schema(description = "Ano de publicação", example = "2008")
    Integer year,

    @Schema(description = "Descrição ou sinopse do livro", example = "Um guia sobre boas práticas de programação.")
    String description,

    @Schema(description = "Data e hora de cadastro do livro no sistema")
    LocalDateTime createdAt
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getDescription(),
                book.getCreatedAt()
        );
    }
}
