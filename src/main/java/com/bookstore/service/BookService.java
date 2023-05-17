package com.bookstore.service;

import com.bookstore.model.db.book.Book;
import com.bookstore.repo.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepository repository;

    public List<Book> getAllBooks() {
        return repository.findAll();
    }

    public boolean saveBook(Book book) {
        return repository.save(book).getId() == book.getId();
    }
}
