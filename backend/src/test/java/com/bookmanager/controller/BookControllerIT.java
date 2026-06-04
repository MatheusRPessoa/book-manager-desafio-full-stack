package com.bookmanager.controller;

import com.bookmanager.AbstractIntegrationTest;
import com.bookmanager.dto.auth.RegisterRequest;
import com.bookmanager.dto.book.BookRequest;
import com.bookmanager.repository.BookRepository;
import com.bookmanager.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerIT extends AbstractIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;

    private String tokenUser1;
    private String tokenUser2;
    private Long bookId;

    @BeforeEach
    void setUp() throws Exception {
        bookRepository.deleteAll();
        userRepository.deleteAll();

        tokenUser1 = registerAndGetToken("user1@example.com", "User One");
        tokenUser2 = registerAndGetToken("user2@example.com", "User Two");
        bookId = createBookAndGetId(tokenUser1, "Clean Code", "Robert C. Martin");
    }

    @Test
    void listBooks_shouldReturn200_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/books")
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"));
    }

    @Test
    void listBooks_shouldReturn403_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listBooks_shouldFilterByTitle() throws Exception {
        createBookAndGetId(tokenUser1, "The Pragmatic Programmer", "David Thomas");

        mockMvc.perform(get("/books?title=pragmatic")
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("The Pragmatic Programmer"));
    }

    @Test
    void createBook_shouldReturn201_whenValidRequest() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Domain-Driven Design");
        request.setAuthor("Eric Evans");
        request.setYear(2003);

        mockMvc.perform(post("/books")
                        .cookie(new Cookie("token", tokenUser1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Domain-Driven Design"))
                .andExpect(jsonPath("$.author").value("Eric Evans"));
    }

    @Test
    void createBook_shouldReturn400_whenTitleIsBlank() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("");
        request.setAuthor("Eric Evans");

        mockMvc.perform(post("/books")
                        .cookie(new Cookie("token", tokenUser1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void getById_shouldReturn200_whenBookBelongsToUser() throws Exception {
        mockMvc.perform(get("/books/" + bookId)
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookId))
                .andExpect(jsonPath("$.title").value("Clean Code"));
    }

    @Test
    void getById_shouldReturn403_whenBookBelongsToAnotherUser() throws Exception {
        mockMvc.perform(get("/books/" + bookId)
                        .cookie(new Cookie("token", tokenUser2)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getById_shouldReturn404_whenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/books/99999")
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBook_shouldReturn200_whenOwner() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code - Updated");
        request.setAuthor("Robert C. Martin");

        mockMvc.perform(put("/books/" + bookId)
                        .cookie(new Cookie("token", tokenUser1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code - Updated"));
    }

    @Test
    void updateBook_shouldReturn403_whenNotOwner() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("Hacked");
        request.setAuthor("Hacker");

        mockMvc.perform(put("/books/" + bookId)
                        .cookie(new Cookie("token", tokenUser2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBook_shouldReturn204_whenOwner() throws Exception {
        mockMvc.perform(delete("/books/" + bookId)
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBook_shouldReturn403_whenNotOwner() throws Exception {
        mockMvc.perform(delete("/books/" + bookId)
                        .cookie(new Cookie("token", tokenUser2)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteBook_shouldReturn404_whenBookDoesNotExist() throws Exception {
        mockMvc.perform(delete("/books/99999")
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listBooks_shouldNotShowOtherUsersBooks() throws Exception {
        createBookAndGetId(tokenUser2, "User Two Book", "Author Two");

        mockMvc.perform(get("/books")
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"));
    }

    @Test
    void listBooks_shouldFilterByAuthor() throws Exception {
        createBookAndGetId(tokenUser1, "The Pragmatic Programmer", "David Thomas");

        mockMvc.perform(get("/books?author=martin")
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].author").value("Robert C. Martin"));
    }

    @Test
    void listBooks_shouldFilterByYearRange() throws Exception {
        BookRequest old = new BookRequest();
        old.setTitle("Old Book");
        old.setAuthor("Old Author");
        old.setYear(1950);
        mockMvc.perform(post("/books")
                .cookie(new Cookie("token", tokenUser1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(old)));

        BookRequest recent = new BookRequest();
        recent.setTitle("Recent Book");
        recent.setAuthor("Recent Author");
        recent.setYear(2020);
        mockMvc.perform(post("/books")
                .cookie(new Cookie("token", tokenUser1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recent)));

        mockMvc.perform(get("/books?yearFrom=2000&yearTo=2025")
                        .cookie(new Cookie("token", tokenUser1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Recent Book"));
    }

    @Test
    void updateBook_shouldReturn400_whenTitleIsBlank() throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle("");
        request.setAuthor("Robert C. Martin");

        mockMvc.perform(put("/books/" + bookId)
                        .cookie(new Cookie("token", tokenUser1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    private String registerAndGetToken(String email, String name) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        Cookie tokenCookie = result.getResponse().getCookie("token");
        return tokenCookie != null ? tokenCookie.getValue() : null;
    }

    private Long createBookAndGetId(String token, String title, String author) throws Exception {
        BookRequest request = new BookRequest();
        request.setTitle(title);
        request.setAuthor(author);

        MvcResult result = mockMvc.perform(post("/books")
                        .cookie(new Cookie("token", token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("id").asLong();
    }
}
