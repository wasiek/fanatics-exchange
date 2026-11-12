package com.github.dziki.fanatics.repository;

import com.github.dziki.fanatics.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAccountId(Long accountId);
    List<Order> findByAccountIdAndStatus(Long accountId, String status);
    List<Order> findBySymbolAndStatus(String symbol, String status);
    long countByAccountIdAndStatus(Long accountId, String status);
}
