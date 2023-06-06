
package com.bookstore.model.db.order;
import com.bookstore.model.db.Name;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;



@Data
@Document
@Builder
public class Order {
    private long id;    
    private String title;
    private float price;
    private float quantity;
    private String trackingStatus;
    private Name author;
    private String genre;
    private float stars;
    private String imgs;
    private String format;
    private String currency;
    private String description;
    private boolean isNew;
    private String review;
}
