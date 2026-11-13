package com.github.dziki.fanatics.dto;

import com.github.dziki.fanatics.model.TradingAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradingAccountResponse {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "ACC00001")
    private String accountNumber;
    @Schema(example = "John Doe")
    private String ownerName;
    @Schema(example = "10000.0000")
    private BigDecimal balance;
    @Schema(example = "ACTIVE")
    private String status;
    @Schema(example = "2026-11-13T09:00:00Z")
    private Instant createdAt;
    @Schema(example = "2026-11-13T10:00:00Z")
    private Instant updatedAt;

    public static TradingAccountResponse fromEntity(TradingAccount account) {
        return TradingAccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .ownerName(account.getOwnerName())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
