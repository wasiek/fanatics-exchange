package com.github.dziki.fanatics.dto;

import com.github.dziki.fanatics.model.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long accountId;
    private String symbol;
    private String side;
    private String type;
    private BigDecimal quantity;
    private BigDecimal price;
    private String status;
    private BigDecimal filledQuantity;
    private BigDecimal averageFillPrice;
    private Instant createdAt;
    private Instant filledAt;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .accountId(order.getAccount() != null ? order.getAccount().getId() : null)
                .symbol(order.getSymbol())
                .side(order.getSide())
                .type(order.getType())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .status(order.getStatus())
                .filledQuantity(order.getFilledQuantity())
                .averageFillPrice(order.getAverageFillPrice())
                .createdAt(order.getCreatedAt())
                .filledAt(order.getFilledAt())
                .build();
    }
}
