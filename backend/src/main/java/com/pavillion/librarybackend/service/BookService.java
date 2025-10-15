package com.pavillion.librarybackend.service;

import com.pavillion.librarybackend.dto.BookDTO;
import com.pavillion.librarybackend.entity.Book;
import com.pavillion.librarybackend.exception.DuplicateIsbnException;
import com.pavillion.librarybackend.exception.ResourceNotFoundException;
import com.pavillion.librarybackend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer handling business logic for Book operations.
 * Implements separation of concerns and encapsulates domain logic.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Creates a new book after validating ISBN uniqueness.
     */
    public BookDTO createBook(BookDTO bookDTO) {
        validateIsbnUnique(bookDTO.getIsbn());
        Book book = mapToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return mapToDTO(savedBook);
    }

    /**
     * Retrieves all books from the database.
     */
    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a single book by ID.
     */
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        Book book = findBookOrThrow(id);
        return mapToDTO(book);
    }

    /**
     * Updates an existing book's details.
     */
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book existingBook = findBookOrThrow(id);

        // Check ISBN uniqueness if ISBN is being changed
        if (!existingBook.getIsbn().equals(bookDTO.getIsbn())) {
            if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
                throw new DuplicateIsbnException("ISBN already exists: " + bookDTO.getIsbn());
            }
        }

        // Update fields
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setPublishedDate(bookDTO.getPublishedDate());

        Book updatedBook = bookRepository.save(existingBook);
        return mapToDTO(updatedBook);
    }

    /**
     * Deletes a book by ID.
     */
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    // Helper methods

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    private void validateIsbnUnique(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateIsbnException("ISBN already exists: " + isbn);
        }
    }

    private BookDTO mapToDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publishedDate(book.getPublishedDate())
                .build();
    }

    private Book mapToEntity(BookDTO dto) {
        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .isbn(dto.getIsbn())
                .publishedDate(dto.getPublishedDate())
                .build();
    }
}