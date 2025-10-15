package com.pavillion.librarybackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for Book API operations.
 * Separates API contract from internal entity structure.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author must not exceed 100 characters")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?:\\d{10}|\\d{13})$",
            message = "ISBN must be 10 or 13 digits")
    private String isbn;

    @PastOrPresent(message = "Published date cannot be in the future")
    private LocalDate publishedDate;
}
