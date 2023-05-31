package com.bookstore;

import com.bookstore.model.db.book.Book;
import com.bookstore.model.db.order.Order;
import com.bookstore.repo.BookRepository;
import com.bookstore.utils.InitDataLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@Slf4j
public class BookStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(BookRepository repository, ApplicationContext context) {
        return args -> {
            final InitDataLoader initDataLoader = context.getBean(InitDataLoader.class);
            final List<Book> books = initDataLoader.getInitBooks();

            if (!books.isEmpty()) {
                log.info("Deleting old contents of the database.");
                repository.deleteAll();

                for (Book book : books) {
                    final Optional<Book> savedBookInDb = repository.findBookById(book.getId());
                    if (savedBookInDb.isPresent()) {
                        log.debug("Book {}:'{}' already exists.", book.getId(), book.getTitle());
                    } else {
                        repository.insert(book);
                        log.debug("Inserted book {}:'{}'.", book.getId(), book.getTitle());
                    }
                }

                log.info("Inserted {} books in the database.", books.size());
            } else {
                log.warn("No books returned by the data loader, not deleting the contents of old database.");
            }
        };
    }
}
