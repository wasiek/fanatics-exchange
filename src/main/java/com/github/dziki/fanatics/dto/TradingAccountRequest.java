package com.github.dziki.fanatics.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradingAccountRequest {

    @NotBlank(message = "Account number is required")
    @Size(min = 8, max = 20, message = "Account number must be 8-20 characters")
    private String accountNumber;

    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be non-negative")
    private BigDecimal initialBalance;
}
