package com.eliteschool.store_service.controller;

import com.eliteschool.common_utils.security.GatewayHeaders;
import com.eliteschool.store_service.client.TaskServiceClient;
import com.eliteschool.store_service.dto.StoreItemDto;
import com.eliteschool.store_service.model.enums.AcquisitionType;
import com.eliteschool.store_service.service.StoreService;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

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
    private JsonMapper objectMapper;

    @MockitoBean
    private StoreService storeService;

    @MockitoBean
    private TaskServiceClient taskServiceClient;

    private StoreItemDto testItem;
    private UUID itemId;
    private UUID studentId;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        itemId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        adminId = UUID.randomUUID();

        testItem = new StoreItemDto();
        testItem.setId(itemId);
        testItem.setName("Test Item");
        testItem.setDescription("Test Description");
        testItem.setPrice(100);
        testItem.setStock(10);
        testItem.setImageUrl("http://example.com/image.jpg");
        testItem.setAcquisitionType(AcquisitionType.POINTS);
    }

    private RequestPostProcessor asAdmin() {
        return request -> {
            request.addHeader(GatewayHeaders.USER_ID, adminId.toString());
            request.addHeader(GatewayHeaders.ROLE, "ADMIN");
            request.addHeader(GatewayHeaders.USERNAME, "admin");
            return request;
        };
    }

    private RequestPostProcessor asManagement() {
        return request -> {
            request.addHeader(GatewayHeaders.USER_ID, adminId.toString());
            request.addHeader(GatewayHeaders.ROLE, "MANAGEMENT");
            request.addHeader(GatewayHeaders.USERNAME, "management");
            return request;
        };
    }

    private RequestPostProcessor asStudent() {
        return request -> {
            request.addHeader(GatewayHeaders.USER_ID, studentId.toString());
            request.addHeader(GatewayHeaders.ROLE, "STUDENT");
            request.addHeader(GatewayHeaders.USERNAME, "student");
            return request;
        };
    }

    private RequestPostProcessor asInternal() {
        return request -> {
            request.addHeader(GatewayHeaders.INTERNAL_SERVICE, "wallet-service");
            return request;
        };
    }

    @Test
    @DisplayName("Should get all items successfully")
    void shouldGetAllItemsSuccessfully() throws Exception {
        List<StoreItemDto> items = Arrays.asList(testItem);
        when(storeService.getAllItems()).thenReturn(items);

        mockMvc.perform(get("/api/store/items").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Test Item"))
                .andExpect(jsonPath("$.data[0].price").value(100));

        verify(storeService, times(1)).getAllItems();
    }

    @Test
    @DisplayName("Should get student catalog with enrichment")
    void shouldGetStudentCatalog() throws Exception {
        when(storeService.getCatalogForStudent(studentId)).thenReturn(List.of(testItem));

        mockMvc.perform(get("/api/store/items").with(asStudent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(storeService, times(1)).getCatalogForStudent(studentId);
    }

    @Test
    @DisplayName("Should get item by ID successfully")
    void shouldGetItemByIdSuccessfully() throws Exception {
        when(storeService.getItemById(itemId)).thenReturn(Optional.of(testItem));

        mockMvc.perform(get("/api/store/items/{itemId}", itemId).with(asStudent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(itemId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Item"));

        verify(storeService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("Should return not found for non-existent item")
    void shouldReturnNotFoundForNonExistentItem() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(storeService.getItemById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/store/items/{itemId}", nonExistentId).with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("ITEM_NOT_FOUND"));

        verify(storeService, times(1)).getItemById(nonExistentId);
    }

    @Test
    @DisplayName("Should add item successfully")
    void shouldAddItemSuccessfully() throws Exception {
        when(storeService.addItem(any(StoreItemDto.class))).thenReturn(testItem);

        mockMvc.perform(post("/api/store/items")
                        .with(asAdmin())
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
        StoreItemDto updatedItem = new StoreItemDto();
        updatedItem.setName("Updated Item");
        updatedItem.setPrice(150);
        updatedItem.setStock(5);
        updatedItem.setAcquisitionType(AcquisitionType.POINTS);

        when(storeService.updateItem(eq(itemId), any(StoreItemDto.class)))
                .thenReturn(Optional.of(updatedItem));

        mockMvc.perform(put("/api/store/items/{itemId}", itemId)
                        .with(asManagement())
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
        UUID nonExistentId = UUID.randomUUID();
        when(storeService.updateItem(eq(nonExistentId), any(StoreItemDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/store/items/{itemId}", nonExistentId)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItem)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("ITEM_NOT_FOUND"));

        verify(storeService, times(1)).updateItem(eq(nonExistentId), any(StoreItemDto.class));
    }

    @Test
    @DisplayName("Should delete item successfully")
    void shouldDeleteItemSuccessfully() throws Exception {
        doNothing().when(storeService).deleteItem(itemId);

        mockMvc.perform(delete("/api/store/items/{itemId}", itemId).with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Item deleted"));

        verify(storeService, times(1)).deleteItem(itemId);
    }

    @Test
    @DisplayName("Should purchase item successfully")
    void shouldPurchaseItemSuccessfully() throws Exception {
        when(storeService.purchaseItem(eq(itemId), eq(studentId), any())).thenReturn(Optional.of(testItem));

        mockMvc.perform(post("/api/store/purchase/{itemId}", itemId)
                        .with(asInternal())
                        .param("studentId", studentId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Purchase successful"));

        verify(storeService, times(1)).purchaseItem(eq(itemId), eq(studentId), any());
    }

    @Test
    @DisplayName("Should fail purchase when item out of stock")
    void shouldFailPurchaseWhenItemOutOfStock() throws Exception {
        when(storeService.purchaseItem(eq(itemId), eq(studentId), any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/store/purchase/{itemId}", itemId)
                        .with(asInternal())
                        .param("studentId", studentId.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.errorCode").value("PURCHASE_FAILED"));

        verify(storeService, times(1)).purchaseItem(eq(itemId), eq(studentId), any());
    }

    @Test
    @DisplayName("Should get item price successfully")
    void shouldGetItemPriceSuccessfully() throws Exception {
        when(storeService.getItemById(itemId)).thenReturn(Optional.of(testItem));

        mockMvc.perform(get("/api/store/items/{itemId}/price", itemId).with(asStudent()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(100));

        verify(storeService, times(1)).getItemById(itemId);
    }

    @Test
    @DisplayName("Should fail to add item with invalid data")
    void shouldFailToAddItemWithInvalidData() throws Exception {
        StoreItemDto invalidItem = new StoreItemDto();

        mockMvc.perform(post("/api/store/items")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }
}
