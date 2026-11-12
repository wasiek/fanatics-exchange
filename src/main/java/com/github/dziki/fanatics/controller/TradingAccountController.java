package com.github.dziki.fanatics.controller;

import com.github.dziki.fanatics.dto.TradingAccountRequest;
import com.github.dziki.fanatics.dto.TradingAccountResponse;
import com.github.dziki.fanatics.service.TradingAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class TradingAccountController {

    private final TradingAccountService accountService;

    @PostMapping
    public ResponseEntity<TradingAccountResponse> createAccount(@Valid @RequestBody TradingAccountRequest request) {
        TradingAccountResponse response = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TradingAccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradingAccountResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<TradingAccountResponse> findByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.findByAccountNumber(accountNumber));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<TradingAccountResponse> deposit(@PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        return ResponseEntity.ok(accountService.deposit(id, amount));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<TradingAccountResponse> withdraw(@PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal amount = request.get("amount");
        return ResponseEntity.ok(accountService.withdraw(id, amount));
    }
}
