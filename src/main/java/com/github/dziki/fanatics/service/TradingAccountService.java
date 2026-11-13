package com.github.dziki.fanatics.service;

import com.github.dziki.fanatics.dto.TradingAccountRequest;
import com.github.dziki.fanatics.dto.TradingAccountResponse;
import com.github.dziki.fanatics.model.TradingAccount;
import com.github.dziki.fanatics.repository.TradingAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingAccountService {

    private final TradingAccountRepository accountRepository;

    @Transactional
    public TradingAccountResponse createAccount(TradingAccountRequest request) {
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + request.getAccountNumber());
        }

        TradingAccount account = TradingAccount.builder()
                .accountNumber(request.getAccountNumber())
                .ownerName(request.getOwnerName())
                .balance(request.getInitialBalance())
                .status("ACTIVE")
                .build();

        account = accountRepository.save(account);
        return TradingAccountResponse.fromEntity(account);
    }

    @Transactional(readOnly = true)
    public List<TradingAccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(TradingAccountResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TradingAccountResponse findById(Long id) {
        TradingAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
        return TradingAccountResponse.fromEntity(account);
    }

    @Transactional(readOnly = true)
    public TradingAccountResponse findByAccountNumber(String accountNumber) {
        TradingAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        return TradingAccountResponse.fromEntity(account);
    }

    @Transactional
    public TradingAccountResponse deposit(Long id, BigDecimal amount) {
        TradingAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        account.setBalance(account.getBalance().add(amount));
        account = accountRepository.save(account);
        log.info("Deposit: account={}, amount={}, newBalance={}", id, amount, account.getBalance());
        return TradingAccountResponse.fromEntity(account);
    }

    @Transactional
    public TradingAccountResponse withdraw(Long id, BigDecimal amount) {
        TradingAccount account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        account = accountRepository.save(account);
        log.info("Withdraw: account={}, amount={}, newBalance={}", id, amount, account.getBalance());
        return TradingAccountResponse.fromEntity(account);
    }
}
