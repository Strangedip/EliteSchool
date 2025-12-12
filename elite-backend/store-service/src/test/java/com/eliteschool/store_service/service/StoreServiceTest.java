package com.eliteschool.store_service.service;

import com.eliteschool.store_service.dto.StoreItemDto;
import com.eliteschool.store_service.model.StoreItem;
import com.eliteschool.store_service.repository.StoreItemRepository;
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

    @InjectMocks
    private StoreService storeService;

    private StoreItem testItem;
    private StoreItemDto testItemDto;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();

        testItem = new StoreItem();
        testItem.setId(itemId);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100);
        testItem.setStock(10);
        testItem.setImageUrl("http://example.com/image.jpg");

        testItemDto = new StoreItemDto();
        testItemDto.setId(itemId);
        testItemDto.setName("Test Item");
        testItemDto.setDescription("Test Description");
        testItemDto.setPrice(100);
        testItemDto.setStock(10);
        testItemDto.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    @DisplayName("Should get all items successfully")
    void shouldGetAllItemsSuccessfully() {
        // Arrange
        List<StoreItem> items = Arrays.asList(testItem);
        when(storeItemRepository.findAll()).thenReturn(items);

        // Act
        List<StoreItemDto> allItems = storeService.getAllItems();

        // Assert
        assertThat(allItems).hasSize(1);
        assertThat(allItems.get(0).getName()).isEqualTo("Test Item");
        verify(storeItemRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get item by ID successfully")
    void shouldGetItemByIdSuccessfully() {
        // Arrange
        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

        // Act
        Optional<StoreItemDto> foundItem = storeService.getItemById(itemId);

        // Assert
        assertThat(foundItem).isPresent();
        assertThat(foundItem.get().getId()).isEqualTo(itemId);
        assertThat(foundItem.get().getName()).isEqualTo("Test Item");
        verify(storeItemRepository, times(1)).findById(itemId);
    }

    @Test
    @DisplayName("Should return empty when item not found")
    void shouldReturnEmptyWhenItemNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(storeItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<StoreItemDto> foundItem = storeService.getItemById(nonExistentId);

        // Assert
        assertThat(foundItem).isEmpty();
        verify(storeItemRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should add item successfully")
    void shouldAddItemSuccessfully() {
        // Arrange
        when(storeItemRepository.save(any(StoreItem.class))).thenReturn(testItem);

        // Act
        StoreItemDto addedItem = storeService.addItem(testItemDto);

        // Assert
        assertThat(addedItem).isNotNull();
        assertThat(addedItem.getName()).isEqualTo("Test Item");
        assertThat(addedItem.getPrice()).isEqualTo(100);
        verify(storeItemRepository, times(1)).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should update item successfully")
    void shouldUpdateItemSuccessfully() {
        // Arrange
        StoreItemDto updatedDto = new StoreItemDto();
        updatedDto.setName("Updated Item");
        updatedDto.setDescription("Updated Description");
        updatedDto.setPrice(150);
        updatedDto.setStock(20);
        updatedDto.setImageUrl("http://example.com/new-image.jpg");

        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(storeItemRepository.save(any(StoreItem.class))).thenReturn(testItem);

        // Act
        Optional<StoreItemDto> updatedItem = storeService.updateItem(itemId, updatedDto);

        // Assert
        assertThat(updatedItem).isPresent();
        verify(storeItemRepository, times(1)).findById(itemId);
        verify(storeItemRepository, times(1)).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should return empty when updating non-existent item")
    void shouldReturnEmptyWhenUpdatingNonExistentItem() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(storeItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<StoreItemDto> updatedItem = storeService.updateItem(nonExistentId, testItemDto);

        // Assert
        assertThat(updatedItem).isEmpty();
        verify(storeItemRepository, times(1)).findById(nonExistentId);
        verify(storeItemRepository, never()).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should delete item successfully")
    void shouldDeleteItemSuccessfully() {
        // Arrange
        doNothing().when(storeItemRepository).deleteById(itemId);

        // Act
        storeService.deleteItem(itemId);

        // Assert
        verify(storeItemRepository, times(1)).deleteById(itemId);
    }

    @Test
    @DisplayName("Should decrement stock successfully")
    void shouldDecrementStockSuccessfully() {
        // Arrange
        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));
        when(storeItemRepository.save(any(StoreItem.class))).thenReturn(testItem);

        // Act
        Optional<StoreItemDto> updatedItem = storeService.decrementStock(itemId);

        // Assert
        assertThat(updatedItem).isPresent();
        verify(storeItemRepository, times(1)).findById(itemId);
        verify(storeItemRepository, times(1)).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should return empty when decrementing stock of out-of-stock item")
    void shouldReturnEmptyWhenDecrementingStockOfOutOfStockItem() {
        // Arrange
        testItem.setStock(0);
        when(storeItemRepository.findById(itemId)).thenReturn(Optional.of(testItem));

        // Act
        Optional<StoreItemDto> updatedItem = storeService.decrementStock(itemId);

        // Assert
        assertThat(updatedItem).isEmpty();
        verify(storeItemRepository, times(1)).findById(itemId);
        verify(storeItemRepository, never()).save(any(StoreItem.class));
    }

    @Test
    @DisplayName("Should return empty when decrementing stock of non-existent item")
    void shouldReturnEmptyWhenDecrementingStockOfNonExistentItem() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(storeItemRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<StoreItemDto> updatedItem = storeService.decrementStock(nonExistentId);

        // Assert
        assertThat(updatedItem).isEmpty();
        verify(storeItemRepository, times(1)).findById(nonExistentId);
        verify(storeItemRepository, never()).save(any(StoreItem.class));
    }
}

