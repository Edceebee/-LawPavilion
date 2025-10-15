package com.pavillion.librarybackend.config;

import com.pavillion.librarybackend.entity.Book;
import com.pavillion.librarybackend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Optional: Initializes the database with sample data on startup.
 * Useful for testing and demonstration purposes.
 * Comment out @Component annotation to disable.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            log.info("Initializing database with sample books...");

            List<Book> sampleBooks = List.of(
                    Book.builder()
                            .title("Clean Code")
                            .author("Robert C. Martin")
                            .isbn("9780132350884")
                            .publishedDate(LocalDate.of(2008, 8, 1))
                            .build(),

                    Book.builder()
                            .title("The Pragmatic Programmer")
                            .author("Andrew Hunt")
                            .isbn("9780135957059")
                            .publishedDate(LocalDate.of(2019, 9, 13))
                            .build(),

                    Book.builder()
                            .title("Design Patterns")
                            .author("Erich Gamma")
                            .isbn("9780201633610")
                            .publishedDate(LocalDate.of(1994, 10, 31))
                            .build(),

                    Book.builder()
                            .title("Effective Java")
                            .author("Joshua Bloch")
                            .isbn("9780134685991")
                            .publishedDate(LocalDate.of(2017, 12, 27))
                            .build(),

                    Book.builder()
                            .title("Refactoring")
                            .author("Martin Fowler")
                            .isbn("9780134757599")
                            .publishedDate(LocalDate.of(2018, 11, 20))
                            .build()
            );

            bookRepository.saveAll(sampleBooks);
            log.info("Database initialized with {} books", sampleBooks.size());
        } else {
            log.info("Database already contains data. Skipping initialization.");
        }
    }
}