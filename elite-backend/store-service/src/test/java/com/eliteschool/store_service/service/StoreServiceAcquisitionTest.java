package com.eliteschool.store_service.service;

import com.eliteschool.common_utils.exception.AppException;
import com.eliteschool.store_service.client.TaskServiceClient;
import com.eliteschool.store_service.dto.StoreItemDto;
import com.eliteschool.store_service.model.StoreItem;
import com.eliteschool.store_service.model.enums.AcquisitionType;
import com.eliteschool.store_service.repository.StoreItemRepository;
import com.eliteschool.store_service.repository.StorePurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreService acquisition tests")
class StoreServiceAcquisitionTest {

    @Mock
    private StoreItemRepository storeItemRepository;
    @Mock
    private StorePurchaseRepository storePurchaseRepository;
    @Mock
    private TaskServiceClient taskServiceClient;

    @InjectMocks
    private StoreService storeService;

    private UUID taskA;
    private UUID taskB;

    @BeforeEach
    void setUp() {
        taskA = UUID.randomUUID();
        taskB = UUID.randomUUID();
    }

    @Test
    void pointsItemRequiresPositivePrice() {
        StoreItemDto dto = StoreItemDto.builder()
                .name("Notebook")
                .price(0)
                .stock(5)
                .acquisitionType(AcquisitionType.POINTS)
                .build();

        assertThrows(AppException.class, () -> storeService.addItem(dto));
        verify(storeItemRepository, never()).save(any());
    }

    @Test
    void tasksItemRequiresLinkedTasks() {
        StoreItemDto dto = StoreItemDto.builder()
                .name("Europe Trip")
                .price(0)
                .stock(1)
                .acquisitionType(AcquisitionType.TASKS)
                .requiredTaskIds(List.of())
                .build();

        assertThrows(AppException.class, () -> storeService.addItem(dto));
    }

    @Test
    void tasksItemAllowsZeroPrice() {
        StoreItemDto dto = StoreItemDto.builder()
                .name("Europe Trip")
                .price(0)
                .stock(1)
                .acquisitionType(AcquisitionType.TASKS)
                .requiredTaskIds(List.of(taskA, taskB))
                .build();

        when(storeItemRepository.save(any(StoreItem.class))).thenAnswer(inv -> {
            StoreItem item = inv.getArgument(0);
            item.setId(UUID.randomUUID());
            return item;
        });

        StoreItemDto saved = storeService.addItem(dto);
        assertEquals(AcquisitionType.TASKS, saved.getAcquisitionType());
        assertEquals(0, saved.getPrice());
        assertEquals(2, saved.getRequiredTaskIds().size());
    }

    @Test
    void pointsAndTasksRequiresBoth() {
        StoreItemDto dto = StoreItemDto.builder()
                .name("Lab Kit")
                .price(50)
                .stock(10)
                .acquisitionType(AcquisitionType.POINTS_AND_TASKS)
                .requiredTaskIds(List.of(taskA))
                .build();

        when(storeItemRepository.save(any(StoreItem.class))).thenAnswer(inv -> {
            StoreItem item = inv.getArgument(0);
            item.setId(UUID.randomUUID());
            return item;
        });

        StoreItemDto saved = storeService.addItem(dto);
        assertEquals(AcquisitionType.POINTS_AND_TASKS, saved.getAcquisitionType());
        assertEquals(50, saved.getPrice());
    }

    @Test
    void sameStudentCanPurchaseTwiceWhileStockRemains() {
        UUID itemId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        StoreItem item = StoreItem.builder()
                .id(itemId)
                .name("Notebook")
                .price(10)
                .stock(3)
                .acquisitionType(AcquisitionType.POINTS)
                .build();

        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(storeItemRepository.save(any(StoreItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(storePurchaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertTrue(storeService.purchaseItem(itemId, studentId, "Student").isPresent());
        assertEquals(2, item.getStock());
        assertTrue(storeService.purchaseItem(itemId, studentId, "Student").isPresent());
        assertEquals(1, item.getStock());

        verify(storePurchaseRepository, times(2)).save(any());
    }

    @Test
    void purchaseFailsWhenOutOfStock() {
        UUID itemId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        StoreItem item = StoreItem.builder()
                .id(itemId)
                .name("Trip")
                .price(0)
                .stock(0)
                .acquisitionType(AcquisitionType.TASKS)
                .requiredTaskIds(List.of(taskA))
                .build();

        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertTrue(storeService.purchaseItem(itemId, studentId, "Student").isEmpty());
        verify(storePurchaseRepository, never()).save(any());
    }

    @Test
    void pointsItemClearsRequiredTasks() {
        StoreItemDto dto = StoreItemDto.builder()
                .name("Pen")
                .price(5)
                .stock(20)
                .acquisitionType(AcquisitionType.POINTS)
                .requiredTaskIds(List.of(taskA))
                .build();

        when(storeItemRepository.save(any(StoreItem.class))).thenAnswer(inv -> {
            StoreItem item = inv.getArgument(0);
            item.setId(UUID.randomUUID());
            return item;
        });

        storeService.addItem(dto);

        ArgumentCaptor<StoreItem> captor = ArgumentCaptor.forClass(StoreItem.class);
        verify(storeItemRepository).save(captor.capture());
        assertTrue(captor.getValue().getRequiredTaskIds().isEmpty());
    }
}
