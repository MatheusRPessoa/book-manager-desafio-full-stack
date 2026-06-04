package com.bookmanager.controller;

import com.bookmanager.AbstractIntegrationTest;
import com.bookmanager.dto.auth.LoginRequest;
import com.bookmanager.dto.auth.RegisterRequest;
import com.bookmanager.repository.BookRepository;
import com.bookmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerIT extends AbstractIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;

    @BeforeEach
    void cleanUp() {
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void register_shouldReturn201_whenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(not(emptyString())))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(cookie().exists("token"))
                .andExpect(cookie().httpOnly("token", true));
    }

    @Test
    void register_shouldReturn409_whenEmailAlreadyInUse() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already in use"));
    }

    @Test
    void register_shouldReturn400_whenNameIsBlank() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void login_shouldReturn200_whenValidCredentials() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setName("John Doe");
        reg.setEmail("john@example.com");
        reg.setPassword("password123");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        LoginRequest login = new LoginRequest();
        login.setEmail("john@example.com");
        login.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(not(emptyString())))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(cookie().exists("token"))
                .andExpect(cookie().httpOnly("token", true));
    }

    @Test
    void login_shouldReturn401_whenWrongPassword() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setName("John Doe");
        reg.setEmail("john@example.com");
        reg.setPassword("password123");
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        LoginRequest login = new LoginRequest();
        login.setEmail("john@example.com");
        login.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_shouldReturn400_whenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("not-an-email");
        request.setPassword("password123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void register_shouldReturn400_whenPasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void login_shouldReturn401_whenEmailNotRegistered() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("notregistered@example.com");
        login.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

}
