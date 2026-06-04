package com.bookmanager.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para criação ou atualização de um livro")
public class BookRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must be at most 500 characters")
    @Schema(description = "Título do livro", example = "Clean Code", maxLength = 500)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 150, message = "Author must be at most 150 characters")
    @Schema(description = "Nome do autor", example = "Robert C. Martin", maxLength = 150)
    private String author;

    @Min(value = 1000, message = "Year must be 1000 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    @Schema(description = "Ano de publicação", example = "2008", minimum = "1000", maximum = "2100")
    private Integer year;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    @Schema(description = "Descrição ou sinopse do livro", example = "Um guia sobre boas práticas de programação.", maxLength = 5000)
    private String description;
}
