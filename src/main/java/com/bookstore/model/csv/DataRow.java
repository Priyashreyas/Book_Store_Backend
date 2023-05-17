package com.bookstore.model.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.ToString;

@ToString
public class DataRow {
    @CsvBindByName
    public String image;
    @CsvBindByName
    public String name;
    @CsvBindByName
    public String author;
    @CsvBindByName
    public String format;
    @CsvBindByName
    public float book_depository_stars;
    @CsvBindByName
    public String price;
    @CsvBindByName
    public String currency;
    @CsvBindByName
    public String old_price;
    @CsvBindByName
    public long isbn;
    @CsvBindByName
    public String category;
    @CsvBindByName
    public String img_paths;
}
