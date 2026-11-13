package com.github.dziki.fanatics.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
        var payload = Map.of(
                "orderId", orderId,
                "symbol", symbol,
                "side", side,
                "quantity", quantity,
                "price", price,
                "commission", commission,
                "executedAt", Instant.now().toString()
        );
        kafkaTemplate.send(TOPIC, String.valueOf(orderId), payload);
        log.info("Published trade-execution to Kafka: topic={}, orderId={}, {} {} @ {}",
                TOPIC, orderId, side, quantity, price);
    }
}
