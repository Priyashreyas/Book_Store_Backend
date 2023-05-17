package com.bookstore.controller;

import com.bookstore.controller.api.BookRequest;
import com.bookstore.controller.api.BookResponse;
import com.bookstore.model.db.book.Book;
import com.bookstore.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/books")
@AllArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<BookResponse> fetchSomeBooks(@RequestParam(value = "count") Optional<Integer> count) {
        if (!count.isPresent() || count.get() < 1) {
            return ResponseEntity.badRequest()
                    .body(BookResponse.builder()
                            .message("Parameter count must have a value > 0.")
                            .build());
        }

        List<Book> books = bookService.getAllBooks();
        log.info("Returning {} books.", count.orElse(books.size()));
        return ResponseEntity.ok(BookResponse.builder()
                .books(count.map(integer -> books.subList(0, integer))
                        .orElse(Collections.emptyList()))
                .build());
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity<BookResponse> fetchBook(@PathVariable long id) {
        log.info("Returning book with id {}.", id);
        List<Book> books = bookService.getAllBooks();
        List<Book> filteredBook = books.stream()
                .filter(book -> book.getId() == id)
                .collect(Collectors.toList());

        if (filteredBook.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(BookResponse.builder()
                            .message(String.format("Book with id %s does not exist.", id))
                            .build());
        }

        return ResponseEntity.ok(BookResponse.builder()
                .book(filteredBook.get(0))
                .build());
    }

    @PreAuthorize("hasRole(T(com.bookstore.model.db.auth.Role).ROLE_ADMIN)")
    @PostMapping
    public ResponseEntity<BookResponse> saveBook(@RequestBody BookRequest request) {
        log.debug("Request: {}", request);
        final Book book = request.getBook();
        log.debug("Book = {}", book);
        if (book == null) {
            log.info("Book is null.");
            return ResponseEntity.badRequest().body(BookResponse.builder()
                    .message("Book is empty.")
                    .build());
        }
        log.info("Saving book with id {}.", book.getId());

        if (!bookService.saveBook(request.getBook())) {
            ResponseEntity.internalServerError()
                    .body(BookResponse.builder()
                            .message(String.format("Could not save the book with id %s.", book.getId()))
                            .build());
        }

        return ResponseEntity.ok()
                .body(BookResponse.builder()
                        .message(String.format("Book with id %s saved successfully.", book.getId()))
                        .build());
    }
}
