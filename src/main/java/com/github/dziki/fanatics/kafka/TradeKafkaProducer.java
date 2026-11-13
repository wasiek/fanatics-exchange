package com.github.dziki.fanatics.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeKafkaProducer {

    private static final String TOPIC = "trade-executions";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishTradeExecuted(Long orderId, String symbol, String side,
                                     BigDecimal quantity, BigDecimal price, BigDecimal commission) {
        Map<String, Object> payload = Map.of(
                "orderId", orderId,
                "symbol", symbol,
                "side", side,
                "quantity", quantity,
                "price", price,
                "commission", commission,
                "executedAt", Instant.now().toString()
        );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    doSend(orderId, payload);
                }
            });
        } else {
            doSend(orderId, payload);
        }
    }

    private void doSend(Long orderId, Map<String, Object> payload) {
        kafkaTemplate.send(TOPIC, String.valueOf(orderId), payload);
        log.info("Published trade-execution to Kafka: topic={}, orderId={}", TOPIC, orderId);
    }
}
