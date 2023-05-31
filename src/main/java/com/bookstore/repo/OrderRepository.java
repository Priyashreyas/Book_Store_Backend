package com.bookstore.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bookstore.model.db.order.Order;

import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, Long> {
    //Optional<Order> retrieveOrders();
}
