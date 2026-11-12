package com.github.dziki.fanatics.repository;

import com.github.dziki.fanatics.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByOrderId(Long orderId);
    List<Trade> findBySymbol(String symbol);
    long countBySymbol(String symbol);
}
