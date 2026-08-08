package com.eliteschool.store_service.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreService Tests")
class StoreServiceTest {

    @Mock
    private StoreItemRepository storeItemRepository;
    @Mock
    private StorePurchaseRepository storePurchaseRepository;
    @Mock
    private TaskServiceClient taskServiceClient;

    @InjectMocks
    private StoreService storeService;

    private StoreItem testItem;
    private StoreItemDto testItemDto;
    private UUID itemId;
    private UUID studentId;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        testItem = new StoreItem();
        testItem.setId(itemId);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100);
        testItem.setStock(10);
        testItem.setImageUrl("http://example.com/image.jpg");
        testItem.setAcquisitionType(AcquisitionType.POINTS);

        testItemDto = new StoreItemDto();
        testItemDto.setId(itemId);
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setPrice(100);
        testItemDto.setStock(10);
        testItemDto.setImageUrl("http://example.com/image.jpg");
        testItemDto.setAcquisitionType(AcquisitionType.POINTS);
    }

    @Test
    @DisplayName("Should get all items successfully")
    void shouldGetAllItemsSuccessfully() {
        List<StoreItem> items = Arrays.asList(testItem);
        when(storeItemRepository.findAll()).thenReturn(items);

        List<StoreItemDto> allItems = storeService.getAllItems();

        assertThat(allItems).hasSize(1);
        assertThat(allItems.get(0).getName()).isEqualTo("Test Item");
        verify(storeItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get item by ID successfully")
    void shouldGetItemByIdSuccessfully() {
        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

        Optional<StoreItemDto> foundItem = storeService.getItemById(itemId);

        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getId()).isEqualTo(itemId);
        assertThat(foundItem.get().getName()).isEqualTo("Test Item");
        verify(storeItemRepository, times(1)).findById(itemId);
    }

    @Test
    @DisplayName("Should return empty when item not found")
    void shouldReturnEmptyWhenItemNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(storeItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<StoreItemDto> foundItem = storeService.getItemById(nonExistentId);

        assertThat(foundItem).isEmpty();
        verify(storeItemRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should add item successfully")
    void shouldAddItemSuccessfully() {
        when(storeItemRepository.save(any(StoreItem.class))).thenReturn(testItem);

        StoreItemDto addedItem = storeService.addItem(testItemDto);

        assertThat(addedItem).isNotNull();
        assertThat(addedItem.getName()).isEqualTo("Test Item");
        assertThat(addedItem.getPrice()).isEqualTo(100);
        verify(storeItemRepository, times(1)).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should update item successfully")
    void shouldUpdateItemSuccessfully() {
        StoreItemDto updatedDto = new StoreItemDto();
        updatedDto.setName("Updated Item");
        updatedDto.setDescription("Updated Description");
        updatedDto.setPrice(150);
        updatedDto.setStock(20);
        updatedDto.setImageUrl("http://example.com/new-image.jpg");
        updatedDto.setAcquisitionType(AcquisitionType.POINTS);

        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(storeItemRepository.save(any(StoreItem.class))).thenReturn(testItem);

        Optional<StoreItemDto> updatedItem = storeService.updateItem(itemId, updatedDto);

        assertThat(updatedItem).isPresent();
        verify(storeItemRepository, times(1)).findById(itemId);
        verify(storeItemRepository, times(1)).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should return empty when updating non-existent item")
    void shouldReturnEmptyWhenUpdatingNonExistentItem() {
        UUID nonExistentId = UUID.randomUUID();
        when(storeItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<StoreItemDto> updatedItem = storeService.updateItem(nonExistentId, testItemDto);

        assertThat(updatedItem).isEmpty();
        verify(storeItemRepository, times(1)).findById(nonExistentId);
        verify(storeItemRepository, never()).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should delete item successfully")
    void shouldDeleteItemSuccessfully() {
        doNothing().when(storeItemRepository).deleteById(itemId);

        storeService.deleteItem(itemId);

        verify(storeItemRepository, times(1)).deleteById(itemId);
    }

    @Test
    @DisplayName("Should purchase and decrement stock successfully")
    void shouldPurchaseSuccessfully() {
        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(storeItemRepository.save(any(StoreItem.class))).thenReturn(testItem);
        when(storePurchaseRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<StoreItemDto> updatedItem = storeService.purchaseItem(itemId, studentId, "Student");

        assertThat(updatedItem).isPresent();
        verify(storeItemRepository, times(1)).findById(itemId);
        verify(storeItemRepository, times(1)).save(any(StoreItem.class));
        verify(storePurchaseRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return empty when purchasing out-of-stock item")
    void shouldReturnEmptyWhenPurchasingOutOfStockItem() {
        testItem.setStock(0);
        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

        Optional<StoreItemDto> updatedItem = storeService.purchaseItem(itemId, studentId, "Student");

        assertThat(updatedItem).isEmpty();
        verify(storeItemRepository, times(1)).findById(itemId);
        verify(storeItemRepository, never()).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should return empty when purchasing non-existent item")
    void shouldReturnEmptyWhenPurchasingNonExistentItem() {
        UUID nonExistentId = UUID.randomUUID();
        when(storeItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<StoreItemDto> updatedItem = storeService.purchaseItem(nonExistentId, studentId, "Student");

        assertThat(updatedItem).isEmpty();
        verify(storeItemRepository, times(1)).findById(nonExistentId);
        verify(storeItemRepository, never()).save(any(StoreItem.class));
    }
}
