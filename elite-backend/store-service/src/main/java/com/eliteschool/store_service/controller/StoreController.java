package com.eliteschool.store_service.controller;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.security.GatewayAuth;
import com.eliteschool.common_utils.util.ResponseUtil;
import com.eliteschool.store_service.dto.StoreItemDto;
import com.eliteschool.store_service.dto.StorePurchaseDto;
import com.eliteschool.store_service.service.StoreService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Slf4j
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/items")
    public ResponseEntity<CommonResponseDto<List<StoreItemDto>>> getAllItems(HttpServletRequest request) {
        if (GatewayAuth.isInternal(request)) {
            return ResponseUtil.success("Items retrieved", storeService.getAllItems());
        }
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        String role = GatewayAuth.role(request);
        if ("STUDENT".equals(role)) {
            UUID studentId = GatewayAuth.requireUserId(request);
            return ResponseUtil.success("Items retrieved", storeService.getCatalogForStudent(studentId));
        }
        return ResponseUtil.success("Items retrieved", storeService.getAllItems());
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> getItemById(@PathVariable UUID itemId,
                                                                       HttpServletRequest request) {
        if (!GatewayAuth.isInternal(request)) {
            GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        }
        return storeService.getItemById(itemId)
                .map(item -> ResponseUtil.success("Item retrieved", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND",
                        "Item not found: " + itemId, null));
    }

    @PostMapping("/items")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> addItem(@Valid @RequestBody StoreItemDto storeItemDto,
                                                                   HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        log.info("Adding item: {}", storeItemDto.getName());
        return ResponseUtil.success("Item added", storeService.addItem(storeItemDto));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> updateItem(
            @PathVariable UUID itemId,
            @Valid @RequestBody StoreItemDto updatedItemDto,
            HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        return storeService.updateItem(itemId, updatedItemDto)
                .map(item -> ResponseUtil.success("Item updated", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND",
                        "Item not found: " + itemId, null));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CommonResponseDto<Void>> deleteItem(@PathVariable UUID itemId,
                                                              HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        try {
            storeService.deleteItem(itemId);
            return ResponseUtil.success("Item deleted", null);
        } catch (Exception e) {
            log.error("Error deleting item {}: {}", itemId, e.getMessage());
            return ResponseUtil.error(HttpStatus.INTERNAL_SERVER_ERROR, "DELETE_ERROR", e.getMessage(), null);
        }
    }

    @PostMapping("/purchase/{itemId}")
    public ResponseEntity<CommonResponseDto<StoreItemDto>> purchaseItem(
            @PathVariable UUID itemId,
            @RequestParam UUID studentId,
            @RequestParam(required = false) String studentName,
            HttpServletRequest request) {
        GatewayAuth.requireInternal(request);
        return storeService.purchaseItem(itemId, studentId, studentName)
                .map(item -> ResponseUtil.success("Purchase successful", item))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.BAD_REQUEST, "PURCHASE_FAILED",
                        "Item out of stock or not found", "Could not claim this item. It may be out of stock or unavailable."));
    }

    @GetMapping("/purchases/student/{studentId}")
    public ResponseEntity<CommonResponseDto<List<StorePurchaseDto>>> getPurchasesForStudent(
            @PathVariable UUID studentId,
            HttpServletRequest request) {
        GatewayAuth.requireSelfOrRoles(request, studentId, "ADMIN", "MANAGEMENT", "FACULTY");
        return ResponseUtil.success("Purchases retrieved", storeService.getPurchasesForStudent(studentId));
    }

    @GetMapping("/purchases")
    public ResponseEntity<CommonResponseDto<List<StorePurchaseDto>>> getAllPurchases(HttpServletRequest request) {
        GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT");
        return ResponseUtil.success("Purchases retrieved", storeService.getAllPurchases());
    }

    @GetMapping("/items/{itemId}/price")
    public ResponseEntity<CommonResponseDto<Integer>> getItemPrice(@PathVariable UUID itemId,
                                                                   HttpServletRequest request) {
        if (!GatewayAuth.isInternal(request)) {
            GatewayAuth.requireRoles(request, "ADMIN", "MANAGEMENT", "FACULTY", "STUDENT");
        }
        return storeService.getItemById(itemId)
                .map(item -> ResponseUtil.success("Price retrieved", item.getPrice()))
                .orElseGet(() -> ResponseUtil.error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND",
                        "Item not found: " + itemId, null));
    }
}
