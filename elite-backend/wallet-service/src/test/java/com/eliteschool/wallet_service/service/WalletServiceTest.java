package com.eliteschool.wallet_service.service;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.wallet_service.client.StoreServiceClient;
import com.eliteschool.wallet_service.dto.TransactionDto;
import com.eliteschool.wallet_service.exception.InsufficientPointsException;
import com.eliteschool.wallet_service.model.Transaction;
import com.eliteschool.wallet_service.model.Wallet;
import com.eliteschool.wallet_service.model.enums.TransactionType;
import com.eliteschool.wallet_service.repository.TransactionRepository;
import com.eliteschool.wallet_service.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WalletService Tests")
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private StoreServiceClient storeServiceClient;

    @InjectMocks
    private WalletService walletService;

    private Wallet testWallet;
    private UUID studentId;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        testWallet = new Wallet();
        testWallet.setStudentId(studentId);
        testWallet.setBalance(100);
    }

    @Test
    @DisplayName("Should get wallet balance successfully")
    void shouldGetWalletBalanceSuccessfully() {
        // Arrange
        when(walletRepository.findByStudentId(studentId)).thenReturn(Optional.of(testWallet));

        // Act
        int balance = walletService.getWalletBalance(studentId);

        // Assert
        assertThat(balance).isEqualTo(100);
        verify(walletRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    @DisplayName("Should return zero balance when wallet not found")
    void shouldReturnZeroBalanceWhenWalletNotFound() {
        // Arrange
        UUID newStudentId = UUID.randomUUID();
        when(walletRepository.findByStudentId(newStudentId)).thenReturn(Optional.empty());

        // Act
        int balance = walletService.getWalletBalance(newStudentId);

        // Assert
        assertThat(balance).isEqualTo(0);
        verify(walletRepository, times(1)).findByStudentId(newStudentId);
    }

    @Test
    @DisplayName("Should credit points successfully to existing wallet")
    void shouldCreditPointsSuccessfullyToExistingWallet() {
        // Arrange
        when(walletRepository.findByStudentId(studentId)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        walletService.creditPoints(studentId, 50, "Test credit");

        // Assert
        verify(walletRepository, times(1)).findByStudentId(studentId);
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create new wallet when crediting points to new student")
    void shouldCreateNewWalletWhenCreditingPointsToNewStudent() {
        // Arrange
        UUID newStudentId = UUID.randomUUID();
        when(walletRepository.findByStudentId(newStudentId)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(new Wallet(newStudentId, 50));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        walletService.creditPoints(newStudentId, 50, "First credit");

        // Assert
        verify(walletRepository, times(1)).findByStudentId(newStudentId);
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should debit points successfully when sufficient balance")
    void shouldDebitPointsSuccessfullyWhenSufficientBalance() {
        // Arrange
        when(walletRepository.findByStudentId(studentId)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        boolean result = walletService.debitPoints(studentId, 30, "Test debit");

        // Assert
        assertThat(result).isTrue();
        verify(walletRepository, times(1)).findByStudentId(studentId);
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient points")
    void shouldThrowExceptionWhenInsufficientPoints() {
        // Arrange
        when(walletRepository.findByStudentId(studentId)).thenReturn(Optional.of(testWallet));

        // Act & Assert
        assertThatThrownBy(() -> walletService.debitPoints(studentId, 200, "Test debit"))
                .isInstanceOf(InsufficientPointsException.class)
                .hasMessageContaining("Insufficient points");

        verify(walletRepository, times(1)).findByStudentId(studentId);
        verify(walletRepository, never()).save(any(Wallet.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when wallet not found for debit")
    void shouldThrowExceptionWhenWalletNotFoundForDebit() {
        // Arrange
        UUID newStudentId = UUID.randomUUID();
        when(walletRepository.findByStudentId(newStudentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> walletService.debitPoints(newStudentId, 50, "Test debit"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Wallet not found");

        verify(walletRepository, times(1)).findByStudentId(newStudentId);
    }

    @Test
    @DisplayName("Should purchase item successfully")
    void shouldPurchaseItemSuccessfully() {
        // Arrange
        CommonResponseDto<Object> priceResponse = new CommonResponseDto<>();
        priceResponse.setSuccess(true);
        priceResponse.setData(50);
        
        CommonResponseDto<Object> purchaseResponse = new CommonResponseDto<>();
        purchaseResponse.setSuccess(true);
        purchaseResponse.setData("Purchase successful");

        when(walletRepository.findByStudentId(studentId)).thenReturn(Optional.of(testWallet));
        when(storeServiceClient.getItemPrice(itemId)).thenReturn(ResponseEntity.ok(priceResponse));
        when(storeServiceClient.purchaseItem(itemId)).thenReturn(ResponseEntity.ok(purchaseResponse));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        // Act
        boolean result = walletService.purchaseItem(studentId, itemId);

        // Assert
        assertThat(result).isTrue();
        verify(storeServiceClient, times(1)).getItemPrice(itemId);
        verify(storeServiceClient, times(1)).purchaseItem(itemId);
        verify(walletRepository, times(1)).save(any(Wallet.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception when item not found during purchase")
    void shouldThrowExceptionWhenItemNotFoundDuringPurchase() {
        // Arrange
        CommonResponseDto<Object> errorResponse = new CommonResponseDto<>();
        errorResponse.setSuccess(false);

        when(storeServiceClient.getItemPrice(itemId)).thenReturn(ResponseEntity.ok(errorResponse));

        // Act & Assert
        assertThatThrownBy(() -> walletService.purchaseItem(studentId, itemId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Item not found");

        verify(storeServiceClient, times(1)).getItemPrice(itemId);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    @DisplayName("Should get transaction history")
    void shouldGetTransactionHistory() {
        // Arrange
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .studentId(studentId)
                .transactionType(TransactionType.CREDIT)
                .points(50)
                .description("Test transaction")
                .build();

        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionRepository.findByStudentIdOrderByCreatedAtDesc(studentId))
                .thenReturn(transactions);

        // Act
        List<TransactionDto> history = walletService.getTransactionHistory(studentId);

        // Assert
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getPoints()).isEqualTo(50);
        verify(transactionRepository, times(1)).findByStudentIdOrderByCreatedAtDesc(studentId);
    }
}

