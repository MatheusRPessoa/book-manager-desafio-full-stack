package com.bookmanager.service;

import com.bookmanager.dto.book.*;
import com.bookmanager.entity.*;
import com.bookmanager.exception.ResourceNotFoundException;
import com.bookmanager.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookResponse> listBooks(String title, String author, Integer yearFrom, Integer yearTo,
                                        User currentUser, Pageable pageable) {
        return bookRepository.findWithFilters(
                currentUser.getId(),
                (title != null && !title.isBlank()) ? title : "",
                (author != null && !author.isBlank()) ? author : "",
                yearFrom,
                yearTo,
                pageable
        ).map(BookResponse::from);
    }

    public BookResponse findById(Long id, User currentUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book"));
        if (!book.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to view this book");
        }
        return BookResponse.from(book);
    }

    @Transactional
    public BookResponse create(BookRequest request, User user) {
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .year(request.getYear())
                .description(request.getDescription())
                .user(user)
                .build();
        return BookResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest request, User currentUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book"));
        if (!book.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to modify this book");
        }
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setYear(request.getYear());
        book.setDescription(request.getDescription());
        return BookResponse.from(book);
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book"));
        if (!book.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this book");
        }
        bookRepository.delete(book);
    }
}
