package com.eliteschool.wallet_service.service;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.wallet_service.client.AuthServiceClient;
import com.eliteschool.wallet_service.client.StoreServiceClient;
import com.eliteschool.wallet_service.client.TaskServiceClient;
import com.eliteschool.wallet_service.exception.TasksNotCompletedException;
import com.eliteschool.wallet_service.model.Wallet;
import com.eliteschool.wallet_service.repository.TransactionRepository;
import com.eliteschool.wallet_service.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WalletService purchase modes")
class WalletServicePurchaseTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private StoreServiceClient storeServiceClient;
    @Mock
    private TaskServiceClient taskServiceClient;
    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private WalletService walletService;

    @Test
    void pointsOnlyDebitsAndPurchases() {
        UUID studentId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        StoreServiceClient.StoreItemView item = StoreServiceClient.StoreItemView.builder()
                .id(itemId)
                .name("Notebook")
                .price(25)
                .stock(3)
                .acquisitionType("POINTS")
                .windowStatus("OPEN")
                .withinClaimWindow(true)
                .requiredTaskIds(List.of())
                .build();

        when(storeServiceClient.getItem(itemId))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok", item)));
        when(walletRepository.findByStudentId(studentId))
                .thenReturn(Optional.of(new Wallet(studentId, 100)));
        when(walletRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storeServiceClient.purchaseItem(eq(itemId), eq(studentId), any()))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok", null)));

        assertTrue(walletService.purchaseItem(studentId, itemId));
        verify(taskServiceClient, never()).checkCompletion(any());
        verify(storeServiceClient).purchaseItem(eq(itemId), eq(studentId), any());
    }

    @Test
    void tasksOnlySkipsDebitWhenEligible() {
        UUID studentId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        StoreServiceClient.StoreItemView item = StoreServiceClient.StoreItemView.builder()
                .id(itemId)
                .name("Europe Trip")
                .price(0)
                .stock(1)
                .acquisitionType("TASKS")
                .windowStatus("OPEN")
                .withinClaimWindow(true)
                .requiredTaskIds(List.of(taskId))
                .build();

        when(storeServiceClient.getItem(itemId))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok", item)));
        when(taskServiceClient.checkCompletion(any()))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok",
                        TaskServiceClient.CompletionCheckResponse.builder().allCompleted(true).build())));
        when(storeServiceClient.purchaseItem(eq(itemId), eq(studentId), any()))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok", null)));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertTrue(walletService.purchaseItem(studentId, itemId));
        verify(walletRepository, never()).save(any());
        verify(storeServiceClient).purchaseItem(eq(itemId), eq(studentId), any());
    }

    @Test
    void tasksOnlyRejectsWhenIncomplete() {
        UUID studentId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        StoreServiceClient.StoreItemView item = StoreServiceClient.StoreItemView.builder()
                .id(itemId)
                .name("Europe Trip")
                .price(0)
                .stock(1)
                .acquisitionType("TASKS")
                .windowStatus("OPEN")
                .withinClaimWindow(true)
                .requiredTaskIds(List.of(taskId))
                .build();

        when(storeServiceClient.getItem(itemId))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok", item)));
        when(taskServiceClient.checkCompletion(any()))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok",
                        TaskServiceClient.CompletionCheckResponse.builder().allCompleted(false).build())));

        assertThrows(TasksNotCompletedException.class,
                () -> walletService.purchaseItem(studentId, itemId));
        verify(storeServiceClient, never()).purchaseItem(any(), any(), any());
    }

    @Test
    void pointsAndTasksRequiresBoth() {
        UUID studentId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        StoreServiceClient.StoreItemView item = StoreServiceClient.StoreItemView.builder()
                .id(itemId)
                .name("Lab Kit")
                .price(40)
                .stock(2)
                .acquisitionType("POINTS_AND_TASKS")
                .windowStatus("OPEN")
                .withinClaimWindow(true)
                .requiredTaskIds(List.of(taskId))
                .build();

        when(storeServiceClient.getItem(itemId))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok", item)));
        when(taskServiceClient.checkCompletion(any()))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok",
                        TaskServiceClient.CompletionCheckResponse.builder().allCompleted(true).build())));
        when(walletRepository.findByStudentId(studentId))
                .thenReturn(Optional.of(new Wallet(studentId, 100)));
        when(walletRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(storeServiceClient.purchaseItem(eq(itemId), eq(studentId), any()))
                .thenReturn(ResponseEntity.ok(CommonResponseDto.success("ok", null)));

        assertTrue(walletService.purchaseItem(studentId, itemId));
        verify(taskServiceClient).checkCompletion(any());
        verify(storeServiceClient).purchaseItem(eq(itemId), eq(studentId), any());
    }
}
