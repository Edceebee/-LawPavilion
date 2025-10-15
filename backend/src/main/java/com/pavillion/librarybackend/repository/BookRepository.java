package com.pavillion.librarybackend.repository;

import com.pavillion.librarybackend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Book entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    Optional<Book> findByIsbn(String isbn);
}