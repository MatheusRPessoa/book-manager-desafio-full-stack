package com.bookmanager.repository;

import com.bookmanager.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE b.user.id = :userId " +
           "AND (:title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
           "AND (:yearFrom IS NULL OR b.year >= :yearFrom) " +
           "AND (:yearTo IS NULL OR b.year <= :yearTo)")
    Page<Book> findWithFilters(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("author") String author,
            @Param("yearFrom") Integer yearFrom,
            @Param("yearTo") Integer yearTo,
            Pageable pageable);
}
