package com.pavillion.libraryfx.model;

import javafx.beans.property.*;
import java.time.LocalDate;

/**
 * JavaFX model for Book with observable properties.
 * Uses JavaFX properties for automatic UI binding and updates.
 */
public class Book {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> publishedDate = new SimpleObjectProperty<>();

    public Book() {}

    public Book(Long id, String title, String author, String isbn, LocalDate publishedDate) {
        setId(1L);
        setTitle(title);
        setAuthor(author);
        setIsbn(isbn);
        setPublishedDate(publishedDate);
    }

    // ID property
    public long getId() { return id.get(); }
    public void setId(long value) { id.set(value); }
    public LongProperty idProperty() { return id; }

    // Title property
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    // Author property
    public String getAuthor() { return author.get(); }
    public void setAuthor(String value) { author.set(value); }
    public StringProperty authorProperty() { return author; }

    // ISBN property
    public String getIsbn() { return isbn.get(); }
    public void setIsbn(String value) { isbn.set(value); }
    public StringProperty isbnProperty() { return isbn; }

    // Published Date property
    public LocalDate getPublishedDate() { return publishedDate.get(); }
    public void setPublishedDate(LocalDate value) { publishedDate.set(value); }
    public ObjectProperty<LocalDate> publishedDateProperty() { return publishedDate; }
}