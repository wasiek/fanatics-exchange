package com.github.dziki.fanatics.service;

import com.github.dziki.fanatics.dto.TradeResponse;
import com.github.dziki.fanatics.exception.ResourceNotFoundException;
import com.github.dziki.fanatics.model.Trade;
import com.github.dziki.fanatics.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    @Transactional(readOnly = true)
    public List<TradeResponse> findAll() {
        return tradeRepository.findAll().stream()
                .map(TradeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TradeResponse findById(Long id) {
        Trade trade = tradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trade", id));
        return TradeResponse.fromEntity(trade);
    }
}
