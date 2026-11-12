package com.github.dziki.fanatics.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private TradingAccount account;

    @Column(nullable = false)
    private String symbol; // e.g., "AAPL", "TSLA"

    @Column(nullable = false)
    private String side; // BUY, SELL

    @Column(nullable = false)
    private String type; // MARKET, LIMIT, STOP

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private String status; // PENDING, FILLED, CANCELLED, REJECTED

    @Column(precision = 19, scale = 4)
    private BigDecimal filledQuantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal averageFillPrice;

    @CreatedDate
    private Instant createdAt;

    private Instant filledAt;
}
