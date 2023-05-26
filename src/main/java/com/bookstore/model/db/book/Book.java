package com.bookstore.model.db.book;

import com.bookstore.model.db.Name;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@Builder
public class Book {
    private long id;
    private String title;
    private float price;
    private float oldPrice;
    private Name author;
    private String genre;
    private float stars;
    private List<String> imgs;
    private String format;
    private String currency;
    private String description;
    private boolean isNew;
    private String review;
}
