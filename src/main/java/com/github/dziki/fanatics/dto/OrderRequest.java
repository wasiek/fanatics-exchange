package com.github.dziki.fanatics.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotBlank(message = "Symbol is required")
    @Size(min = 1, max = 10, message = "Symbol must be 1-10 characters")
    private String symbol;

    @NotBlank(message = "Side is required")
    @Pattern(regexp = "^(BUY|SELL)$", message = "Side must be BUY or SELL")
    private String side;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(MARKET|LIMIT|STOP)$", message = "Type must be MARKET, LIMIT, or STOP")
    private String type;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001", message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;
}
