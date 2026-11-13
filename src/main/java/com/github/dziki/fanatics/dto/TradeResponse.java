package com.github.dziki.fanatics.dto;

import com.github.dziki.fanatics.model.Trade;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeResponse {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "1")
    private Long orderId;
    @Schema(example = "AAPL")
    private String symbol;
    @Schema(example = "BUY")
    private String side;
    @Schema(example = "10.0000")
    private BigDecimal quantity;
    @Schema(example = "150.0000")
    private BigDecimal price;
    @Schema(example = "1.5000")
    private BigDecimal commission;
    @Schema(example = "2026-11-13T10:30:00Z")
    private Instant executedAt;

    public static TradeResponse fromEntity(Trade trade) {
        return TradeResponse.builder()
                .id(trade.getId())
                .orderId(trade.getOrder() != null ? trade.getOrder().getId() : null)
                .symbol(trade.getSymbol())
                .side(trade.getSide())
                .quantity(trade.getQuantity())
                .price(trade.getPrice())
                .commission(trade.getCommission())
                .executedAt(trade.getExecutedAt())
                .build();
    }
}
