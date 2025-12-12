package com.eliteschool.store_service.controller;

import com.eliteschool.store_service.dto.StoreItemDto;
import com.eliteschool.store_service.service.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("StoreController Tests")
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StoreService storeService;

    private StoreItemDto testItem;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();

        testItem = new StoreItemDto();
        testItem.setId(itemId);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100);
        testItem.setStock(10);
        testItem.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    @DisplayName("Should get all items successfully")
    void shouldGetAllItemsSuccessfully() throws Exception {
        // Arrange
        List<StoreItemDto> items = Arrays.asList(testItem);
        when(storeService.getAllItems()).thenReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/store/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Test Item"))
                .andExpect(jsonPath("$.data[0].price").value(100));

        verify(storeService, times(1)).getAllItems();
    }

    @Test
    @DisplayName("Should get item by ID successfully")
    void shouldGetItemByIdSuccessfully() throws Exception {
        // Arrange
        when(storeService.getItemById(itemId)).thenReturn(Optional.of(testItem));

        // Act & Assert
        mockMvc.perform(get("/api/store/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(itemId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Item"));

        verify(storeService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("Should return not found for non-existent item")
    void shouldReturnNotFoundForNonExistentItem() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(storeService.getItemById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/store/items/{itemId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ITEM_NOT_FOUND"));

        verify(storeService, times(1)).getItemById(nonExistentId);
    }

    @Test
    @DisplayName("Should add item successfully")
    void shouldAddItemSuccessfully() throws Exception {
        // Arrange
        when(storeService.addItem(any(StoreItemDto.class))).thenReturn(testItem);

        // Act & Assert
        mockMvc.perform(post("/api/store/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item added"))
                .andExpect(jsonPath("$.data.name").value("Test Item"));

        verify(storeService, times(1)).addItem(any(StoreItemDto.class));
    }

    @Test
    @DisplayName("Should update item successfully")
    void shouldUpdateItemSuccessfully() throws Exception {
        // Arrange
        StoreItemDto updatedItem = new StoreItemDto();
        updatedItem.setName("Updated Item");
        updatedItem.setPrice(150);

        when(storeService.updateItem(eq(itemId), any(StoreItemDto.class)))
                .thenReturn(Optional.of(updatedItem));

        // Act & Assert
        mockMvc.perform(put("/api/store/items/{itemId}", itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item updated"));

        verify(storeService, times(1)).updateItem(eq(itemId), any(StoreItemDto.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent item")
    void shouldReturnNotFoundWhenUpdatingNonExistentItem() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(storeService.updateItem(eq(nonExistentId), any(StoreItemDto.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/store/items/{itemId}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ITEM_NOT_FOUND"));

        verify(storeService, times(1)).updateItem(eq(nonExistentId), any(StoreItemDto.class));
    }

    @Test
    @DisplayName("Should delete item successfully")
    void shouldDeleteItemSuccessfully() throws Exception {
        // Arrange
        doNothing().when(storeService).deleteItem(itemId);

        // Act & Assert
        mockMvc.perform(delete("/api/store/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item deleted"));

        verify(storeService, times(1)).deleteItem(itemId);
    }

    @Test
    @DisplayName("Should purchase item successfully")
    void shouldPurchaseItemSuccessfully() throws Exception {
        // Arrange
        when(storeService.decrementStock(itemId)).thenReturn(Optional.of(testItem));

        // Act & Assert
        mockMvc.perform(post("/api/store/purchase/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Purchase successful"));

        verify(storeService, times(1)).decrementStock(itemId);
    }

    @Test
    @DisplayName("Should fail purchase when item out of stock")
    void shouldFailPurchaseWhenItemOutOfStock() throws Exception {
        // Arrange
        when(storeService.decrementStock(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/api/store/purchase/{itemId}", itemId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("PURCHASE_FAILED"));

        verify(storeService, times(1)).decrementStock(itemId);
    }

    @Test
    @DisplayName("Should get item price successfully")
    void shouldGetItemPriceSuccessfully() throws Exception {
        // Arrange
        when(storeService.getItemById(itemId)).thenReturn(Optional.of(testItem));

        // Act & Assert
        mockMvc.perform(get("/api/store/items/{itemId}/price", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(100));

        verify(storeService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("Should fail to add item with invalid data")
    void shouldFailToAddItemWithInvalidData() throws Exception {
        // Arrange
        StoreItemDto invalidItem = new StoreItemDto();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/store/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }
}

