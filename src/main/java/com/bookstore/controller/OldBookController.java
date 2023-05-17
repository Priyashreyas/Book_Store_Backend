package com.bookstore.controller;

import com.bookstore.model.db.book.Book;
import com.bookstore.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@AllArgsConstructor
@Slf4j
public class OldBookController {
    private final BookService bookService;

    @GetMapping
    public List<Book> fetchSomeBooks(@RequestParam(value = "count") Optional<Integer> count) {
        List<Book> books = bookService.getAllBooks();
        log.info("Returning {} books.", count.orElse(books.size()));
        return count.map(integer -> books.subList(0, integer))
                .orElse(Collections.emptyList());
    }

}
