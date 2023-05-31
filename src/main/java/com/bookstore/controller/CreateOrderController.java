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
@RequestMapping("/api/v1/cart")
@AllArgsConstructor
@Slf4j
public class CreateOrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> saveOrder(@RequestBody OrderRequest request) {
        log.info("Request#####: {}", request);
        final Order order = request.getOrder();
        log.info("Order = {}", order);
        if (order == null) {
            log.info("yhe order null hai.");
            return ResponseEntity.badRequest().body(OrderResponse.builder()
                    .message("Order is empty.")
                    .build());
        }
        log.info("Saving Order with id {}.", order.getId());

        if (!orderService.saveOrder(request.getOrder())) {
            ResponseEntity.internalServerError()
                    .body(OrderResponse.builder()
                            .message(String.format("Could not save the Order with id %s.", order.getId()))
                            .build());
        }

        return ResponseEntity.ok()
                .body(OrderResponse.builder()
                        .message(String.format("Order with id %s saved successfully.", order.getId()))
                        .build());
    }
}
