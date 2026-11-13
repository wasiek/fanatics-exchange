package com.github.dziki.fanatics.controller;

import com.github.dziki.fanatics.dto.TradeResponse;
import com.github.dziki.fanatics.model.Trade;
import com.github.dziki.fanatics.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeRepository tradeRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<TradeResponse>> findAll() {
        List<TradeResponse> trades = tradeRepository.findAll().stream()
                .map(TradeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(trades);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<TradeResponse> findById(@PathVariable Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trade not found: " + id));
        return ResponseEntity.ok(TradeResponse.fromEntity(trade));
    }
}
