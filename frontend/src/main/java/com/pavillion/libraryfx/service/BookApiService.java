package com.pavillion.libraryfx.service;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.pavillion.libraryfx.model.Book;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service class for REST API communication with the backend.
 * Uses Java 11+ HttpClient for modern, non-blocking HTTP operations.
 * Implements proper error handling and JSON serialization.
 */
public class BookApiService {
    private static final String BASE_URL = "http://localhost:8080/api/books";
    private final HttpClient httpClient;
    private final Gson gson;

    public BookApiService() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        // Configure Gson with LocalDate adapter
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    /**
     * Fetches all books from the backend.
     */
    public List<Book> getAllBooks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch books: " + response.statusCode());
        }

        return gson.fromJson(response.body(), new TypeToken<List<Book>>(){}.getType());
    }

    /**
     * Creates a new book on the backend.
     */
    public Book createBook(Book book) throws IOException, InterruptedException {
        String jsonBody = gson.toJson(book);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            handleErrorResponse(response);
        }

        return gson.fromJson(response.body(), Book.class);
    }

    /**
     * Updates an existing book on the backend.
     */
    public Book updateBook(Long id, Book book) throws IOException, InterruptedException {
        String jsonBody = gson.toJson(book);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            handleErrorResponse(response);
        }

        return gson.fromJson(response.body(), Book.class);
    }

    /**
     * Deletes a book from the backend.
     */
    public void deleteBook(Long id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            handleErrorResponse(response);
        }
    }

    /**
     * Handles error responses from the backend.
     */
    private void handleErrorResponse(HttpResponse<String> response) throws IOException {
        JsonObject errorJson = JsonParser.parseString(response.body()).getAsJsonObject();
        String message = errorJson.has("message")
                ? errorJson.get("message").getAsString()
                : "Unknown error occurred";
        throw new IOException(message);
    }

    /**
     * Custom Gson adapter for LocalDate serialization/deserialization.
     */
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>,
            JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public JsonElement serialize(LocalDate date, java.lang.reflect.Type type,
                                     JsonSerializationContext context) {
            return new JsonPrimitive(date.format(FORMATTER));
        }

        @Override
        public LocalDate deserialize(JsonElement json, java.lang.reflect.Type type,
                                     JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString(), FORMATTER);
        }
    }
}