package com.github.dziki.fanatics.service;

import com.github.dziki.fanatics.dto.OrderRequest;
import com.github.dziki.fanatics.dto.OrderResponse;
import com.github.dziki.fanatics.model.Order;
import com.github.dziki.fanatics.model.TradingAccount;
import com.github.dziki.fanatics.model.Trade;
import com.github.dziki.fanatics.repository.OrderRepository;
import com.github.dziki.fanatics.repository.TradeRepository;
import com.github.dziki.fanatics.repository.TradingAccountRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final TradingAccountRepository accountRepository;
    private final TradeRepository tradeRepository;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        // Pessimistic lock to prevent race conditions on balance
        TradingAccount account = accountRepository.findByIdAndLock(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + request.getAccountId()));

        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalArgumentException("Account is not active");
        }

        // For MARKET orders, simulate execution at current price
        BigDecimal executionPrice = request.getPrice() != null ? request.getPrice() : BigDecimal.valueOf(100.0);
        BigDecimal commission = executionPrice.multiply(request.getQuantity()).multiply(BigDecimal.valueOf(0.001));

        // VALIDATE BEFORE ANY WRITE
        if ("BUY".equals(request.getSide())) {
            BigDecimal totalCost = executionPrice.multiply(request.getQuantity()).add(commission);
            if (account.getBalance().compareTo(totalCost) < 0) {
                throw new IllegalArgumentException("Insufficient balance for order");
            }
        }

        // All validations passed - now write
        Order order = Order.builder()
                .account(account)
                .symbol(request.getSymbol())
                .side(request.getSide())
                .type(request.getType())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .status("FILLED")
                .filledQuantity(request.getQuantity())
                .averageFillPrice(executionPrice)
                .filledAt(Instant.now())
                .build();

        order = orderRepository.save(order);

        Trade trade = Trade.builder()
                .order(order)
                .symbol(request.getSymbol())
                .side(request.getSide())
                .quantity(request.getQuantity())
                .price(executionPrice)
                .commission(commission)
                .executedAt(Instant.now())
                .build();

        tradeRepository.save(trade);

        // Update account balance (safe - we hold the lock)
        if ("BUY".equals(request.getSide())) {
            BigDecimal totalCost = executionPrice.multiply(request.getQuantity()).add(commission);
            account.setBalance(account.getBalance().subtract(totalCost));
        } else if ("SELL".equals(request.getSide())) {
            BigDecimal proceeds = executionPrice.multiply(request.getQuantity()).subtract(commission);
            account.setBalance(account.getBalance().add(proceeds));
        }

        accountRepository.save(account);

        log.info("Order placed: {} {} {} @ {} (commission: {}), new balance: {}",
                request.getSide(), request.getQuantity(), request.getSymbol(),
                executionPrice, commission, account.getBalance());

        return OrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return OrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByAccountId(Long accountId) {
        return orderRepository.findByAccountId(accountId).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findBySymbolAndStatus(String symbol, String status) {
        return orderRepository.findBySymbolAndStatus(symbol, status).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalArgumentException("Order cannot be cancelled - status: " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        order = orderRepository.save(order);
        return OrderResponse.fromEntity(order);
    }
}
