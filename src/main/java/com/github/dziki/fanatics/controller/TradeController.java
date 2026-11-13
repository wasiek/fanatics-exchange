package com.github.dziki.fanatics.controller;

import com.github.dziki.fanatics.dto.TradeResponse;
import com.github.dziki.fanatics.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @GetMapping
    public ResponseEntity<Page<TradeResponse>> findAll(
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(tradeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TradeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tradeService.findById(id));
    }
}
