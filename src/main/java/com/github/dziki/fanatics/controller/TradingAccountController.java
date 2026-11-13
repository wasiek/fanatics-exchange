package com.github.dziki.fanatics.controller;

import com.github.dziki.fanatics.dto.DepositRequest;
import com.github.dziki.fanatics.dto.TradingAccountRequest;
import com.github.dziki.fanatics.dto.TradingAccountResponse;
import com.github.dziki.fanatics.dto.WithdrawRequest;
import com.github.dziki.fanatics.service.TradingAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<TradingAccountResponse> deposit(@PathVariable Long id,
                                                          @Valid @RequestBody DepositRequest request) {
        TradingAccountResponse response = accountService.deposit(id, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<TradingAccountResponse> withdraw(@PathVariable Long id,
                                                           @Valid @RequestBody WithdrawRequest request) {
        TradingAccountResponse response = accountService.withdraw(id, request.getAmount());
        return ResponseEntity.ok(response);
    }
}
