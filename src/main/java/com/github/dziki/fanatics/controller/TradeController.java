package com.github.dziki.fanatics.controller;

import com.github.dziki.fanatics.dto.TradeResponse;
import com.github.dziki.fanatics.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    public ResponseEntity<List<TradeResponse>> findAll() {
        return ResponseEntity.ok(tradeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeService.findById(id));
    }
}
