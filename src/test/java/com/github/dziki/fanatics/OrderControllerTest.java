package com.github.dziki.fanatics;

import com.github.dziki.fanatics.dto.OrderRequest;
import com.github.dziki.fanatics.model.Order;
import com.github.dziki.fanatics.model.TradingAccount;
import com.github.dziki.fanatics.repository.OrderRepository;
import com.github.dziki.fanatics.repository.TradeRepository;
import com.github.dziki.fanatics.repository.TradingAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradingAccountRepository accountRepository;

    private TradingAccount account;

    @BeforeEach
    void setUp() {
        tradeRepository.deleteAll();
        orderRepository.deleteAll();
        accountRepository.deleteAll();

        account = TradingAccount.builder()
                .accountNumber("ACC10001")
                .ownerName("TestUser")
                .balance(new BigDecimal("100000"))
                .status("ACTIVE")
                .build();
        account = accountRepository.save(account);
    }

    @Test
    void placeOrder_sell() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .accountId(account.getId())
                .symbol("MSFT")
                .side("SELL")
                .type("MARKET")
                .quantity(new BigDecimal("5"))
                .price(new BigDecimal("300"))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symbol").value("MSFT"))
                .andExpect(jsonPath("$.side").value("SELL"))
                .andExpect(jsonPath("$.status").value("FILLED"));

        // Balance should increase: 100000 + (5 * 300) - (5 * 300 * 0.001) = 100000 + 1500 - 1.5 = 101498.5
        mockMvc.perform(get("/api/v1/accounts/" + account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(101498.5));
    }

    @Test
    void placeOrder_accountNotFound() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .accountId(999L)
                .symbol("AAPL")
                .side("BUY")
                .type("MARKET")
                .quantity(new BigDecimal("1"))
                .price(new BigDecimal("100"))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Account not found: 999"));
    }

    @Test
    void findAllOrders() throws Exception {
        Order order = Order.builder()
                .account(account)
                .symbol("GOOG")
                .side("BUY")
                .type("MARKET")
                .quantity(new BigDecimal("2"))
                .price(new BigDecimal("200"))
                .filledQuantity(new BigDecimal("2"))
                .status("FILLED")
                .build();
        orderRepository.save(order);

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("GOOG"))
                .andExpect(jsonPath("$[0].status").value("FILLED"));
    }

    @Test
    void findOrderById() throws Exception {
        Order order = Order.builder()
                .account(account)
                .symbol("AMZN")
                .side("BUY")
                .type("MARKET")
                .quantity(new BigDecimal("3"))
                .price(new BigDecimal("180"))
                .filledQuantity(new BigDecimal("3"))
                .status("FILLED")
                .build();
        order = orderRepository.save(order);

        mockMvc.perform(get("/api/v1/orders/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("AMZN"))
                .andExpect(jsonPath("$.quantity").value(3.0));
    }

    @Test
    void findOrderById_notFound() throws Exception {
        mockMvc.perform(get("/api/v1/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found: 999"));
    }

    @Test
    void findByAccountId() throws Exception {
        Order order = Order.builder()
                .account(account)
                .symbol("NVDA")
                .side("BUY")
                .type("MARKET")
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("500"))
                .filledQuantity(new BigDecimal("10"))
                .status("FILLED")
                .build();
        orderRepository.save(order);

        mockMvc.perform(get("/api/v1/orders/account/" + account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("NVDA"))
                .andExpect(jsonPath("$[0].quantity").value(10.0));
    }

    @Test
    void findBySymbolAndStatus() throws Exception {
        Order filled = Order.builder()
                .account(account)
                .symbol("TSLA")
                .side("BUY")
                .type("MARKET")
                .quantity(new BigDecimal("5"))
                .price(new BigDecimal("250"))
                .filledQuantity(new BigDecimal("5"))
                .status("FILLED")
                .build();
        Order open = Order.builder()
                .account(account)
                .symbol("TSLA")
                .side("SELL")
                .type("LIMIT")
                .quantity(new BigDecimal("5"))
                .price(new BigDecimal("200"))
                .status("OPEN")
                .build();
        orderRepository.save(filled);
        orderRepository.save(open);

        mockMvc.perform(get("/api/v1/orders/symbol/TSLA/status/FILLED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].side").value("BUY"))
                .andExpect(jsonPath("$[0].status").value("FILLED"));
    }

    @Test
    void cancelOrder() throws Exception {
        Order order = Order.builder()
                .account(account)
                .symbol("META")
                .side("BUY")
                .type("LIMIT")
                .quantity(new BigDecimal("4"))
                .price(new BigDecimal("350"))
                .status("PENDING")
                .build();
        order = orderRepository.save(order);

        mockMvc.perform(post("/api/v1/orders/" + order.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelOrder_alreadyFilled() throws Exception {
        Order order = Order.builder()
                .account(account)
                .symbol("META")
                .side("BUY")
                .type("MARKET")
                .quantity(new BigDecimal("4"))
                .price(new BigDecimal("350"))
                .filledQuantity(new BigDecimal("4"))
                .status("FILLED")
                .build();
        order = orderRepository.save(order);

        mockMvc.perform(post("/api/v1/orders/" + order.getId() + "/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Order cannot be cancelled - status: FILLED"));
    }

    @Test
    void cancelOrder_notFound() throws Exception {
        mockMvc.perform(post("/api/v1/orders/999/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found: 999"));
    }
}
