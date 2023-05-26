package com.bookstore.model.db;
import com.bookstore.model.db.Name;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@Builder
public class reviews {
    private String review;
    private long id;
}
