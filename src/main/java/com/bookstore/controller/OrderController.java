package com.bookstore.controller;

import com.bookstore.controller.api.OrderRequest;
import com.bookstore.controller.api.OrderResponse;
import com.bookstore.model.db.order.Order;
import com.bookstore.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/order")
@AllArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<OrderResponse> fetchSomeOrders(@RequestParam(value = "count") Optional<Integer> count) {
        if (!count.isPresent() || count.get() < 1) {
            return ResponseEntity.badRequest()
                    .body(OrderResponse.builder()
                            .message("Parameter count must have a value > 0.")
                            .build());
        }

        List<Order> orders = orderService.getAllOrders();
        log.info("Returning {} orders.", count.orElse(orders.size()));
        return ResponseEntity.ok(OrderResponse.builder()
                .books(count.map(integer -> orders.subList(0, integer))
                        .orElse(Collections.emptyList()))
                .build());
    }

}
