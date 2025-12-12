package com.eliteschool.wallet_service.controller;

import com.eliteschool.wallet_service.dto.TransactionDto;
import com.eliteschool.wallet_service.model.enums.TransactionType;
import com.eliteschool.wallet_service.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("WalletController Tests")
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    private UUID studentId;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        itemId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should get wallet balance successfully")
    void shouldGetWalletBalanceSuccessfully() throws Exception {
        // Arrange
        when(walletService.getWalletBalance(studentId)).thenReturn(100);

        // Act & Assert
        mockMvc.perform(get("/api/wallet/{studentId}/balance", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(100));

        verify(walletService, times(1)).getWalletBalance(studentId);
    }

    @Test
    @DisplayName("Should credit points successfully")
    void shouldCreditPointsSuccessfully() throws Exception {
        // Arrange
        doNothing().when(walletService).creditPoints(any(UUID.class), anyInt(), anyString());
        when(walletService.getWalletBalance(studentId)).thenReturn(150);

        // Act & Assert
        mockMvc.perform(post("/api/wallet/{studentId}/credit", studentId)
                .param("points", "50")
                .param("description", "Test credit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Points credited"))
                .andExpect(jsonPath("$.data").value(150));

        verify(walletService, times(1)).creditPoints(studentId, 50, "Test credit");
        verify(walletService, times(1)).getWalletBalance(studentId);
    }

    @Test
    @DisplayName("Should debit points successfully")
    void shouldDebitPointsSuccessfully() throws Exception {
        // Arrange
        when(walletService.debitPoints(any(UUID.class), anyInt(), anyString())).thenReturn(true);
        when(walletService.getWalletBalance(studentId)).thenReturn(50);

        // Act & Assert
        mockMvc.perform(post("/api/wallet/{studentId}/debit", studentId)
                .param("points", "50")
                .param("description", "Test debit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Points debited"))
                .andExpect(jsonPath("$.data").value(50));

        verify(walletService, times(1)).debitPoints(studentId, 50, "Test debit");
        verify(walletService, times(1)).getWalletBalance(studentId);
    }

    @Test
    @DisplayName("Should purchase item successfully")
    void shouldPurchaseItemSuccessfully() throws Exception {
        // Arrange
        when(walletService.purchaseItem(studentId, itemId)).thenReturn(true);
        when(walletService.getWalletBalance(studentId)).thenReturn(50);

        // Act & Assert
        mockMvc.perform(post("/api/wallet/{studentId}/purchase/{itemId}", studentId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Purchase successful"))
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.remainingBalance").value(50));

        verify(walletService, times(1)).purchaseItem(studentId, itemId);
        verify(walletService, times(1)).getWalletBalance(studentId);
    }

    @Test
    @DisplayName("Should get transaction history successfully")
    void shouldGetTransactionHistorySuccessfully() throws Exception {
        // Arrange
        TransactionDto transaction1 = new TransactionDto();
        transaction1.setId(UUID.randomUUID());
        transaction1.setStudentId(studentId);
        transaction1.setTransactionType(TransactionType.CREDIT);
        transaction1.setPoints(50);
        transaction1.setDescription("Test credit");

        TransactionDto transaction2 = new TransactionDto();
        transaction2.setId(UUID.randomUUID());
        transaction2.setStudentId(studentId);
        transaction2.setTransactionType(TransactionType.DEBIT);
        transaction2.setPoints(20);
        transaction2.setDescription("Test debit");

        List<TransactionDto> transactions = Arrays.asList(transaction1, transaction2);
        when(walletService.getTransactionHistory(studentId)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/api/wallet/{studentId}/transactions", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].transactionType").value("CREDIT"))
                .andExpect(jsonPath("$.data[1].transactionType").value("DEBIT"));

        verify(walletService, times(1)).getTransactionHistory(studentId);
    }

    @Test
    @DisplayName("Should award task points successfully")
    void shouldAwardTaskPointsSuccessfully() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "studentId": "%s",
                    "points": 50,
                    "description": "Task completion reward"
                }
                """.formatted(studentId);

        doNothing().when(walletService).creditPoints(any(UUID.class), anyInt(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/wallet/award")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Points awarded"));

        verify(walletService, times(1)).creditPoints(eq(studentId), eq(50), anyString());
    }
}

