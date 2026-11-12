package com.github.dziki.fanatics;

import com.github.dziki.fanatics.dto.OrderRequest;
import com.github.dziki.fanatics.dto.TradingAccountRequest;
import com.github.dziki.fanatics.model.TradingAccount;
import com.github.dziki.fanatics.repository.OrderRepository;
import com.github.dziki.fanatics.repository.TradeRepository;
import com.github.dziki.fanatics.repository.TradingAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TradingAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TradingAccountRepository accountRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @BeforeEach
    void setUp() {
        tradeRepository.deleteAll();
        orderRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void createAccount_success() throws Exception {
        TradingAccountRequest request = TradingAccountRequest.builder()
                .accountNumber("ACC00001")
                .ownerName("Alice")
                .initialBalance(new BigDecimal("50000"))
                .build();

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.accountNumber").value("ACC00001"))
                .andExpect(jsonPath("$.ownerName").value("Alice"))
                .andExpect(jsonPath("$.balance").value(50000.0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createAccount_duplicate() throws Exception {
        TradingAccount account = TradingAccount.builder()
                .accountNumber("ACC00002")
                .ownerName("Bob")
                .balance(new BigDecimal("10000"))
                .status("ACTIVE")
                .build();
        accountRepository.save(account);

        TradingAccountRequest request = TradingAccountRequest.builder()
                .accountNumber("ACC00002")
                .ownerName("Bob2")
                .initialBalance(new BigDecimal("20000"))
                .build();

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void deposit_and_withdraw() throws Exception {
        TradingAccount account = TradingAccount.builder()
                .accountNumber("ACC00003")
                .ownerName("Charlie")
                .balance(new BigDecimal("10000"))
                .status("ACTIVE")
                .build();
        account = accountRepository.save(account);

        // Deposit 5000
        mockMvc.perform(post("/api/v1/accounts/" + account.getId() + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 5000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(15000.0));

        // Withdraw 3000
        mockMvc.perform(post("/api/v1/accounts/" + account.getId() + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 3000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(12000.0));
    }

    @Test
    void withdraw_insufficientBalance() throws Exception {
        TradingAccount account = TradingAccount.builder()
                .accountNumber("ACC00004")
                .ownerName("Dave")
                .balance(new BigDecimal("1000"))
                .status("ACTIVE")
                .build();
        account = accountRepository.save(account);

        mockMvc.perform(post("/api/v1/accounts/" + account.getId() + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 5000}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient balance"));
    }

    @Test
    void placeOrder_buy() throws Exception {
        TradingAccount account = TradingAccount.builder()
                .accountNumber("ACC00005")
                .ownerName("Eve")
                .balance(new BigDecimal("100000"))
                .status("ACTIVE")
                .build();
        account = accountRepository.save(account);

        OrderRequest request = OrderRequest.builder()
                .accountId(account.getId())
                .symbol("AAPL")
                .side("BUY")
                .type("MARKET")
                .quantity(new BigDecimal("10"))
                .price(new BigDecimal("150"))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.side").value("BUY"))
                .andExpect(jsonPath("$.status").value("FILLED"))
                .andExpect(jsonPath("$.filledQuantity").value(10.0));

        // Balance should be 100000 - (10 * 150) - (10 * 150 * 0.001) = 100000 - 1500 - 1.5 = 98498.5
        mockMvc.perform(get("/api/v1/accounts/" + account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(98498.5));
    }

    @Test
    void placeOrder_insufficientBalance() throws Exception {
        TradingAccount account = TradingAccount.builder()
                .accountNumber("ACC00006")
                .ownerName("Frank")
                .balance(new BigDecimal("1000"))
                .status("ACTIVE")
                .build();
        account = accountRepository.save(account);

        OrderRequest request = OrderRequest.builder()
                .accountId(account.getId())
                .symbol("TSLA")
                .side("BUY")
                .type("MARKET")
                .quantity(new BigDecimal("100"))
                .price(new BigDecimal("200"))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient balance for order"));
    }
}
