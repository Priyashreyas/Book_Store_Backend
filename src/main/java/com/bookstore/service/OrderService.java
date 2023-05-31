package com.bookstore.service;

import com.bookstore.model.db.order.Order;
import com.bookstore.repo.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository repository;

    public List<Order> getAllOrders() {
        return repository.findAll();
    }
}
