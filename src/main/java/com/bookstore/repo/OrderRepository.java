package com.bookstore.repo;
import com.bookstore.model.db.order.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, Long> {
   
    Optional<Order> findOrderByAuthorFirstName(String title);

    Optional<Order> findOrderById(long id);

    
}


