package com.bookstore.repo;

import com.bookstore.model.db.book.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, Long> {
    Optional<Book> findBookByAuthorFirstName(String firstName);

    Optional<Book> findBookByAuthorLastName(String lastName);

    Optional<Book> findBookById(long id);
}
