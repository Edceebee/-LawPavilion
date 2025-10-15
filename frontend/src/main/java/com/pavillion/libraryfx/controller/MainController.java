package com.pavillion.libraryfx.controller;

import com.pavillion.libraryfx.model.Book;
import com.pavillion.libraryfx.service.BookApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the main Library Management UI.
 * Handles user interactions and coordinates with the API service.
 * Uses async operations to keep UI responsive.
 */
public class MainController {

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, Long> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, LocalDate> publishedDateColumn;

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private DatePicker publishedDatePicker;
    @FXML private TextField searchField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    private final BookApiService apiService;
    private final ObservableList<Book> bookList;
    private final ObservableList<Book> filteredBookList;
    private Book selectedBook;

    public MainController() {
        this.apiService = new BookApiService();
        this.bookList = FXCollections.observableArrayList();
        this.filteredBookList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableSelectionListener();
        setupSearchFilter();
        updateButton.setDisable(true);
        loadBooks();
    }

    /**
     * Configure table columns with property bindings.
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        bookTable.setItems(filteredBookList);
    }

    /**
     * Setup listener for table row selection.
     * Populates form fields when a book is selected.
     */
    private void setupTableSelectionListener() {
        bookTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedBook = newValue;
                        populateForm(newValue);
                        updateButton.setDisable(false);
                    } else {
                        selectedBook = null;
                        updateButton.setDisable(true);
                    }
                });
    }

    /**
     * Setup search filter for title and author.
     * Bonus feature: Real-time search functionality.
     */
    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBooks(newValue);
        });
    }

    /**
     * Filter books based on search query.
     */
    private void filterBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredBookList.setAll(bookList);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            filteredBookList.setAll(
                    bookList.stream()
                            .filter(book ->
                                    book.getTitle().toLowerCase().contains(lowerQuery) ||
                                            book.getAuthor().toLowerCase().contains(lowerQuery))
                            .toList()
            );
        }
    }

    /**
     * Load all books from backend asynchronously.
     */
    @FXML
    private void loadBooks() {
        new Thread(() -> {
            try {
                List<Book> books = apiService.getAllBooks();
                Platform.runLater(() -> {
                    bookList.setAll(books);
                    filterBooks(searchField.getText());
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to load books", e.getMessage()));
            }
        }).start();
    }

    /**
     * Add a new book.
     */
    @FXML
    private void handleAdd() {
        if (!validateForm()) return;

        Book book = createBookFromForm();

        new Thread(() -> {
            try {
                Book created = apiService.createBook(book);
                Platform.runLater(() -> {
                    bookList.add(created);
                    filterBooks(searchField.getText());
                    clearForm();
                    showInfo("Success", "Book added successfully");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to add book", e.getMessage()));
            }
        }).start();
    }

    /**
     * Update selected book.
     */
    @FXML
    private void handleUpdate() {
        if (selectedBook == null) {
            showWarning("No Selection", "Please select a book to update");
            return;
        }

        if (!validateForm()) return;

        Book updatedBook = createBookFromForm();
        Long id = selectedBook.getId();

        new Thread(() -> {
            try {
                Book updated = apiService.updateBook(id, updatedBook);
                Platform.runLater(() -> {
                    int index = bookList.indexOf(selectedBook);
                    if (index >= 0) {
                        bookList.set(index, updated);
                        filterBooks(searchField.getText());
                    }
                    clearForm();
                    showInfo("Success", "Book updated successfully");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to update book", e.getMessage()));
            }
        }).start();
    }

    /**
     * Delete selected book.
     */
    @FXML
    private void handleDelete() {
        if (selectedBook == null) {
            showWarning("No Selection", "Please select a book to delete");
            return;
        }

        Optional<ButtonType> result = showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this book?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Long id = selectedBook.getId();

            new Thread(() -> {
                try {
                    apiService.deleteBook(id);
                    Platform.runLater(() -> {
                        bookList.remove(selectedBook);
                        filterBooks(searchField.getText());
                        clearForm();
                        showInfo("Success", "Book deleted successfully");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Failed to delete book", e.getMessage()));
                }
            }).start();
        }
    }

    /**
     * Clear form fields.
     */
    @FXML
    private void handleClear() {
        clearForm();
    }

    /**
     * Populate form with book data.
     */
    private void populateForm(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        publishedDatePicker.setValue(book.getPublishedDate());
    }

    /**
     * Clear all form fields and reset selection.
     */
    private void clearForm() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        publishedDatePicker.setValue(null);
        bookTable.getSelectionModel().clearSelection();
        selectedBook = null;
        updateButton.setDisable(true);
    }

    /**
     * Create a Book object from form fields.
     */
    private Book createBookFromForm() {
        return new Book(
                null,
                titleField.getText().trim(),
                authorField.getText().trim(),
                isbnField.getText().trim(),
                publishedDatePicker.getValue()
        );
    }

    /**
     * Validate form inputs.
     */
    private boolean validateForm() {
        if (titleField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Title is required");
            return false;
        }
        if (authorField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "Author is required");
            return false;
        }
        if (isbnField.getText().trim().isEmpty()) {
            showWarning("Validation Error", "ISBN is required");
            return false;
        }
        if (!isbnField.getText().trim().matches("\\d{10}|\\d{13}")) {
            showWarning("Validation Error", "ISBN must be 10 or 13 digits");
            return false;
        }
        return true;
    }

    // UI Dialog Helpers

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}