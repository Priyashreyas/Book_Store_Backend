package com.bookstore.service;
//import com.bookstore.controller.api.APIUser;
import com.bookstore.model.db.book.Book;
//import com.bookstore.model.db.auth.book.Book;
import com.bookstore.repo.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
//import java.util.Optional;

@Service
@AllArgsConstructor
public class DeleteBookService {
    private final BookRepository repository;


    public List<Book> getAllBooks() {
        return repository.findAll();
    }


    public void deleteBook(Long id) {
        repository.deleteById(id);
    }
}
