package com.github.dziki.fanatics.dto;

import com.github.dziki.fanatics.model.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "1")
    private Long accountId;
    @Schema(example = "AAPL")
    private String symbol;
    @Schema(example = "BUY")
    private String side;
    @Schema(example = "MARKET")
    private String type;
    @Schema(example = "10.0000")
    private BigDecimal quantity;
    @Schema(example = "150.0000")
    private BigDecimal price;
    @Schema(example = "FILLED")
    private String status;
    @Schema(example = "10.0000")
    private BigDecimal filledQuantity;
    @Schema(example = "150.0000")
    private BigDecimal averageFillPrice;
    @Schema(example = "2026-11-13T10:00:00Z")
    private Instant createdAt;
    @Schema(example = "2026-11-13T10:00:00Z")
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
