package com.pavillion.librarybackend.controller;

import com.pavillion.librarybackend.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * REST Controller exposing Book management endpoints.
 * Uses proper HTTP semantics and status codes.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Enable CORS for JavaFX client
public class BookController {

    private final com.pavillion.librarybackend.service.BookService bookService;

    /**
     * POST /api/books - Create a new book
     * Returns 201 CREATED with the created book in the response body
     */
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        BookDTO created = bookService.createBook(bookDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/books - Retrieve all books
     * Returns 200 OK with list of books
     */
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    /**
     * GET /api/books/{id} - Retrieve a specific book by ID
     * Returns 200 OK if found, 404 NOT FOUND otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    /**
     * PUT /api/books/{id} - Update an existing book
     * Returns 200 OK with updated book
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO bookDTO) {
        BookDTO updated = bookService.updateBook(id, bookDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/books/{id} - Delete a book by ID
     * Returns 204 NO CONTENT on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}