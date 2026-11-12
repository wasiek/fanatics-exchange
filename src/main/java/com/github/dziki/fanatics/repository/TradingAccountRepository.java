package com.github.dziki.fanatics.repository;

import com.github.dziki.fanatics.model.TradingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradingAccountRepository extends JpaRepository<TradingAccount, Long> {
    Optional<TradingAccount> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
