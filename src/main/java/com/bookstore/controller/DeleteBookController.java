package com.bookstore.controller;

import com.bookstore.controller.api.DeletaBookResponse;
import com.bookstore.controller.api.DeleteBookRequest;
//import com.bookstore.controller.api.BookRequest;
//import com.bookstore.controller.api.BookResponse;
import com.bookstore.model.db.book.Book;
//import com.bookstore.service.BookService;
import com.bookstore.service.DeleteBookService;

//import io.micrometer.core.instrument.Meter.Id;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import org.yaml.snakeyaml.events.Event.ID;


//import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/delete")
@AllArgsConstructor
@Slf4j
public class DeleteBookController {
        private final DeleteBookService deleteService;
        //private final BookService bookService;


 /*   @GetMapping
    public ResponseEntity<BookResponse> getAllbooks() {
        List<Book> books = bookService.getAllBooks();
        log.info("Returning {} books.", books.size());
        log.info("is this my method.", books.size());
        return ResponseEntity.ok()
                .body(BookResponse.builder()
                        .build());
    }

*/
@PreAuthorize("hasRole(T(com.bookstore.model.db.auth.Role).ROLE_ADMIN)")
@GetMapping
    public ResponseEntity<DeletaBookResponse> getAllBooks() {
        List<Book> books = deleteService.getAllBooks();
        log.info("Delete Method Returning {} users.", books.size());
        DeletaBookResponse bookResponse = new DeletaBookResponse();
        return ResponseEntity.ok(bookResponse.builder()
                                .books(books.stream()
                                .collect(Collectors.toList()))
                                .build());
        
       /* .builder()
                .books(books.stream()
                        .map(Book::new)
                        .collect(Collectors.toList()))
                .build()); */
    }
    
    


    @DeleteMapping
    public ResponseEntity<DeletaBookResponse> deleteBook(@RequestParam(value = "ID") Optional<Long> id) {
        //log.debug("Username = {}", username.orElse("null"));
        if (!id.isPresent()) {
            log.info("id is null.");
            return ResponseEntity.badRequest().body(DeletaBookResponse.builder()
                    .message("id is empty.")
                    .build());
        }
        log.info("Deleting book with id {}.", id.get());

        try {
            deleteService.deleteBook(id.get());
            return ResponseEntity.ok()
                    .body(DeletaBookResponse.builder()
                            .message(String.format("User with username %s deleted successfully.", id.get()))
                            .build());
        } catch (RuntimeException e) {
            log.error("Unexpected exception: ", e);
        }

        return ResponseEntity.internalServerError()
                .body(DeletaBookResponse.builder()
                        .message(String.format("Could not delete the user with username %s.", id.get()))
                        .build());
    }
}
