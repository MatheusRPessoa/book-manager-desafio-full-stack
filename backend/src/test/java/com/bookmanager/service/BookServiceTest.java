package com.bookmanager.service;

import com.bookmanager.dto.book.BookRequest;
import com.bookmanager.dto.book.BookResponse;
import com.bookmanager.entity.Book;
import com.bookmanager.entity.User;
import com.bookmanager.exception.ResourceNotFoundException;
import com.bookmanager.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock BookRepository bookRepository;

    @InjectMocks BookService bookService;

    User owner;
    User other;
    Book book;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).name("Dono").email("dono@email.com").password("hash").build();
        other = User.builder().id(2L).name("Outro").email("outro@email.com").password("hash").build();
        book = Book.builder().id(10L).title("Clean Code").author("Martin").year(2008).user(owner).build();
    }

    @Test
    void findById_success() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.findById(10L, owner);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.title()).isEqualTo("Clean Code");
    }

    @Test
    void findById_notFound_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findById(99L, owner))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_accessDenied_throwsException() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.findById(10L, other))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void create_success() {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code");
        request.setAuthor("Martin");
        request.setYear(2008);

        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse result = bookService.create(request, owner);

        assertThat(result.title()).isEqualTo("Clean Code");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void update_success() {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code Updated");
        request.setAuthor("Martin");
        request.setYear(2009);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.update(10L, request, owner);

        assertThat(result.title()).isEqualTo("Clean Code Updated");
        assertThat(result.year()).isEqualTo(2009);
    }

    @Test
    void update_notFound_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(99L, new BookRequest(), owner))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_accessDenied_throwsException() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.update(10L, new BookRequest(), other))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void delete_success() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        bookService.delete(10L, owner);

        verify(bookRepository).delete(book);
    }

    @Test
    void delete_notFound_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.delete(99L, owner))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_accessDenied_throwsException() {
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.delete(10L, other))
                .isInstanceOf(AccessDeniedException.class);
    }
}
