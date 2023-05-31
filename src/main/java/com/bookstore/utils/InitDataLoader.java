package com.bookstore.utils;

import com.bookstore.exception.AuthorNotFoundException;
import com.bookstore.exception.PriceNotFoundException;
import com.bookstore.model.csv.DataRow;
import com.bookstore.model.db.Name;
import com.bookstore.model.db.book.Book;
import com.bookstore.model.db.order.Order;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX;

@Slf4j
public class InitDataLoader {

    private static final String DATA_SET_FILE = CLASSPATH_URL_PREFIX + "static/dataset/main_dataset.csv";
    private static final int MAX_BOOKS_PER_AUTHOR = 4;
    private static final int MAX_BOOKS_PER_GENRE = 20;
    private static final Set<String> SUPPORTED_GENRES = ImmutableSet.of(
//                "Crime-Thriller",
            "Technology-Engineering",
//                "Entertainment",
//                "Dictionaries-Languages",
//                "Personal-Development",
//                "Computing",
            "Sport",
//            "Travel-Holiday-Guides",
            "Art-Photography",
            "Biography",
//                "Graphic-Novels-Anime-Manga",
//                "Crafts-Hobbies",
//                "Mind-Body-Spirit",
//                "History-Archaeology",
//                "Business-Finance-Law",
            "Health",
//                "Teaching-Resources-Education",
//                "Reference",
//                "Religion",
//                "Science-Geography",
//                "Romance",
//                "Science-Fiction-Fantasy-Horror",
//                "Stationery",
//                "Poetry-Drama",
//                "Home-Garden",
//                "Teen-Young-Adult",
//                "Food-Drink",
//                "Natural-History",
//                "Transport",
//                "Society-Social-Sciences",
//                "Childrens-Books,"
//                "Medical,"
            "Humour"
    );

    @Qualifier("webApplicationContext")
    @Autowired
    ResourceLoader resourceLoader;

    public List<Book> getInitBooks() {
        final List<DataRow> dataRows = getBooksFromDataSet();
        final List<Book> books = convertDataRowsToBookAndCleanData(dataRows);
        List<Book> booksToBeStored = filterBooksToBeStored(books);
        log.info("Books to be stored = {}.", booksToBeStored.size());
        return booksToBeStored;
    }

    private List<Book> filterBooksToBeStored(List<Book> books) {
        final Map<String, Integer> genreBookCount = new HashMap<>();
        final Map<String, Integer> authorBookCount = new HashMap<>();
        final Set<Long> bookIdSet = new HashSet<>();
        final Set<String> bookTitleSet = new HashSet<>();

        return books.stream()
                .filter(book -> SUPPORTED_GENRES.contains(book.getGenre()) &&
                        !book.getTitle().toLowerCase().contains("f**k") &&
                        genreBookCount.getOrDefault(book.getGenre(), 0) < MAX_BOOKS_PER_GENRE &&
                        authorBookCount.getOrDefault(book.getAuthor().toString(), 0) < MAX_BOOKS_PER_AUTHOR &&
                        !bookIdSet.contains(book.getId()) &&
                        !bookTitleSet.contains(book.getTitle().trim().toLowerCase()))
                .peek(book -> {
                    genreBookCount.put(book.getGenre(), genreBookCount.getOrDefault(book.getGenre(), 0) + 1);
                    final String author = book.getAuthor().toString();
                    authorBookCount.put(author, genreBookCount.getOrDefault(author, 0) + 1);
                    bookIdSet.add(book.getId());
                    bookTitleSet.add(book.getTitle().trim().toLowerCase());
                })
                .collect(Collectors.toList());
    }

    private List<Book> convertDataRowsToBookAndCleanData(List<DataRow> dataRows) {
        return dataRows.stream()
                .map(row -> {
                    Book book = null;

                    try {
                        book = Book.builder()
                                .imgs(ImmutableList.of(row.image.trim(), row.image.trim()))
                                .title(row.name.trim())
                                .author(getAuthorFirstAndLastNames(row.author.trim()))
                                .format(row.format.trim())
                                .stars(row.book_depository_stars)
                                .price(extractPrice(row.price.trim()))
                                .currency(row.currency.trim())
                                .oldPrice(extractPrice(row.old_price.trim()))
                                .id(row.isbn)
                                .genre(row.category.trim())
                                .description(generateBookDescription())
                                .isNew(generateIsNew())
                                .isTrending(generateIsTrending())
                                .isFeatured(generateIsFeatured())
                                .build();
                    } catch (AuthorNotFoundException | PriceNotFoundException e) {
                        log.debug("Encountered exception while processing the book {}: {}", row, e.getMessage());
                    } catch (Exception e) {
                        log.error("Failed to convert data row to book for {}.", row, e);
                    }

                    return book;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private boolean generateIsFeatured() {
        return Math.round(Math.random() * 10) % 2 == 0;
    }

    private boolean generateIsTrending() {
        return Math.round(Math.random() * 10) % 2 == 0;
    }

    private boolean generateIsNew() {
        return Math.round(Math.random() * 10) % 2 == 0;
    }

    private String generateBookDescription() {
        return "Fuga feugiat aliquid dolore rem curae eu tempus maecenas porttitor pede mauris diamlorem integer tempus, iusto, mauris, aliquam quos nibh! Sequi doloremque, accusamus! Eleifend? Eos exercitation faucibus proin labore do volutpat nam? Illum, litora modi augue? Aliquid impedit? Perferendis iste aliquet hac sunt at. Consectetuer. Nemo sunt perferendis, voluptates mollis.";
    }

    private float extractPrice(String price) {
        int start = 0;
        int end = price.length() - 1;
        while (start < price.length() && !Character.isDigit(price.charAt(start))) {
            start++;
        }
        while (end >= 0 && !Character.isDigit(price.charAt(end))) {
            end--;
        }

        if (start > end) {
            throw new PriceNotFoundException("No price found for the book.");
        }

        return Float.parseFloat(price.substring(start, end + 1));
    }

    private Name getAuthorFirstAndLastNames(String name) {
        int i = name.lastIndexOf(" ");
        final String firstName = 0 > i ? "" : name.substring(0, i);
        final String lastName = i + 1 > name.length() ? "" : name.substring(i + 1);

        if (firstName.isEmpty() && lastName.isEmpty()) {
            throw new AuthorNotFoundException("No author found for the book.");
        }

        return new Name(firstName, lastName);
    }

    private List<DataRow> getBooksFromDataSet() {
        try {
            final Resource dataSetFileResource = resourceLoader.getResource(DATA_SET_FILE);

            return new CsvToBeanBuilder<DataRow>(new FileReader(dataSetFileResource.getFile()))
                    .withType(DataRow.class)
                    .build()
                    .parse();
        } catch (IOException e) {
            log.error("Failed to fetch data from the dataset {}.", DATA_SET_FILE, e);
            return Collections.emptyList();
        }
    }
}
