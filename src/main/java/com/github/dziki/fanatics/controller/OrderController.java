package com.github.dziki.fanatics.controller;

import com.github.dziki.fanatics.dto.OrderRequest;
import com.github.dziki.fanatics.dto.OrderResponse;
import com.github.dziki.fanatics.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.ok(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<OrderResponse>> findByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(orderService.findByAccountId(accountId));
    }

    @GetMapping("/symbol/{symbol}/status/{status}")
    public ResponseEntity<List<OrderResponse>> findBySymbolAndStatus(
            @PathVariable String symbol,
            @PathVariable String status) {
        return ResponseEntity.ok(orderService.findBySymbolAndStatus(symbol, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
