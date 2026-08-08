package com.eliteschool.store_service.service;

import com.eliteschool.common_utils.dto.CommonResponseDto;
import com.eliteschool.common_utils.exception.AppException;
import com.eliteschool.store_service.client.TaskServiceClient;
import com.eliteschool.store_service.dto.ClaimInfoDto;
import com.eliteschool.store_service.dto.RequiredTaskProgressDto;
import com.eliteschool.store_service.dto.StoreItemDto;
import com.eliteschool.store_service.dto.StoreItemMapper;
import com.eliteschool.store_service.dto.StorePurchaseDto;
import com.eliteschool.store_service.model.StoreItem;
import com.eliteschool.store_service.model.StorePurchase;
import com.eliteschool.store_service.model.enums.AcquisitionType;
import com.eliteschool.store_service.model.enums.ClaimWindowStatus;
import com.eliteschool.store_service.model.enums.ItemCategory;
import com.eliteschool.store_service.repository.StoreItemRepository;
import com.eliteschool.store_service.repository.StorePurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreItemRepository storeItemRepository;
    private final StorePurchaseRepository storePurchaseRepository;
    private final TaskServiceClient taskServiceClient;

    public List<StoreItemDto> getAllItems() {
        return storeItemRepository.findAll().stream()
                .map(StoreItemMapper::toDto)
                .map(this::enrichWindowAndClaims)
                .toList();
    }

    public List<StoreItemDto> getCatalogForStudent(UUID studentId) {
        List<StoreItemDto> items = getAllItems();
        if (studentId == null || items.isEmpty()) {
            return items;
        }

        Set<UUID> allTaskIds = items.stream()
                .filter(i -> i.getRequiredTaskIds() != null)
                .flatMap(i -> i.getRequiredTaskIds().stream())
                .collect(Collectors.toCollection(HashSet::new));

        Map<UUID, TaskServiceClient.TaskProgress> progressByTask = new HashMap<>();
        if (!allTaskIds.isEmpty()) {
            try {
                ResponseEntity<CommonResponseDto<TaskServiceClient.CompletionCheckResponse>> response =
                        taskServiceClient.checkCompletion(TaskServiceClient.CompletionCheckRequest.builder()
                                .studentId(studentId)
                                .taskIds(new ArrayList<>(allTaskIds))
                                .build());
                if (response.getBody() != null && response.getBody().getData() != null
                        && response.getBody().getData().getTasks() != null) {
                    for (TaskServiceClient.TaskProgress t : response.getBody().getData().getTasks()) {
                        progressByTask.put(t.getTaskId(), t);
                    }
                }
            } catch (Exception ex) {
                log.warn("Could not enrich store catalog with task progress: {}", ex.getMessage());
            }
        }

        return items.stream()
                .map(item -> enrichTaskProgress(item, progressByTask))
                .toList();
    }

    private StoreItemDto enrichWindowAndClaims(StoreItemDto item) {
        ClaimWindowStatus status = resolveWindowStatus(item.getClaimOpensAt(), item.getClaimClosesAt());
        item.setWindowStatus(status);
        item.setWithinClaimWindow(status == ClaimWindowStatus.OPEN);

        if (item.getId() != null) {
            List<ClaimInfoDto> claims = storePurchaseRepository.findByItemIdOrderByClaimedAtDesc(item.getId())
                    .stream()
                    .map(p -> ClaimInfoDto.builder()
                            .studentId(p.getStudentId())
                            .studentName(p.getStudentName())
                            .claimedAt(p.getClaimedAt())
                            .build())
                    .toList();
            item.setClaims(claims);
        } else {
            item.setClaims(List.of());
        }
        return item;
    }

    private StoreItemDto enrichTaskProgress(StoreItemDto item,
                                            Map<UUID, TaskServiceClient.TaskProgress> progressByTask) {
        AcquisitionType type = item.getAcquisitionType() != null
                ? item.getAcquisitionType()
                : AcquisitionType.POINTS;
        List<UUID> required = item.getRequiredTaskIds() != null
                ? item.getRequiredTaskIds()
                : List.of();

        List<RequiredTaskProgressDto> progress = required.stream()
                .map(taskId -> {
                    TaskServiceClient.TaskProgress tp = progressByTask.get(taskId);
                    return RequiredTaskProgressDto.builder()
                            .taskId(taskId)
                            .title(tp != null && tp.getTitle() != null ? tp.getTitle() : "Task")
                            .completed(tp != null && tp.isCompleted())
                            .build();
                })
                .toList();

        boolean tasksOk = progress.isEmpty() || progress.stream().allMatch(RequiredTaskProgressDto::isCompleted);
        boolean eligible = switch (type) {
            case POINTS -> true;
            case TASKS, POINTS_AND_TASKS -> tasksOk;
        };

        item.setRequiredTasks(progress);
        item.setEligible(eligible && Boolean.TRUE.equals(item.getWithinClaimWindow()) && item.getStock() != null && item.getStock() > 0);
        return item;
    }

    public static ClaimWindowStatus resolveWindowStatus(LocalDateTime opensAt, LocalDateTime closesAt) {
        LocalDateTime now = LocalDateTime.now();
        if (closesAt != null && now.isAfter(closesAt)) {
            return ClaimWindowStatus.EXPIRED;
        }
        if (opensAt != null && now.isBefore(opensAt)) {
            return ClaimWindowStatus.NOT_OPEN;
        }
        return ClaimWindowStatus.OPEN;
    }

    public Optional<StoreItemDto> getItemById(UUID itemId) {
        return storeItemRepository.findById(itemId)
                .map(StoreItemMapper::toDto)
                .map(this::enrichWindowAndClaims);
    }

    public StoreItemDto addItem(StoreItemDto itemDto) {
        validateAcquisition(itemDto);
        validateWindows(itemDto);
        StoreItem storeItem = StoreItemMapper.toEntity(itemDto);
        return enrichWindowAndClaims(StoreItemMapper.toDto(storeItemRepository.save(storeItem)));
    }

    @Transactional
    public Optional<StoreItemDto> updateItem(UUID itemId, StoreItemDto updatedItemDto) {
        validateAcquisition(updatedItemDto);
        validateWindows(updatedItemDto);
        return storeItemRepository.findById(itemId).map(existingItem -> {
            existingItem.setName(updatedItemDto.getName());
            existingItem.setPrice(updatedItemDto.getPrice());
            existingItem.setStock(updatedItemDto.getStock());
            existingItem.setDescription(updatedItemDto.getDescription());
            existingItem.setImageUrl(updatedItemDto.getImageUrl());
            AcquisitionType type = updatedItemDto.getAcquisitionType() != null
                    ? updatedItemDto.getAcquisitionType()
                    : AcquisitionType.POINTS;
            existingItem.setAcquisitionType(type);
            existingItem.setItemCategory(updatedItemDto.getItemCategory() != null
                    ? updatedItemDto.getItemCategory()
                    : ItemCategory.MATERIAL);
            existingItem.setClaimOpensAt(updatedItemDto.getClaimOpensAt());
            existingItem.setClaimClosesAt(updatedItemDto.getClaimClosesAt());
            existingItem.setOpportunityBrief(updatedItemDto.getOpportunityBrief());
            existingItem.setIntendedAudience(updatedItemDto.getIntendedAudience());
            existingItem.setEligibilityChecklist(updatedItemDto.getEligibilityChecklist() != null
                    ? new ArrayList<>(updatedItemDto.getEligibilityChecklist())
                    : new ArrayList<>());
            existingItem.setRequiredTaskIds(updatedItemDto.getRequiredTaskIds() != null
                    ? new ArrayList<>(updatedItemDto.getRequiredTaskIds())
                    : new ArrayList<>());
            return enrichWindowAndClaims(StoreItemMapper.toDto(storeItemRepository.save(existingItem)));
        });
    }

    public void deleteItem(UUID itemId) {
        storeItemRepository.deleteById(itemId);
        log.info("Item with ID {} deleted successfully", itemId);
    }

    @Transactional
    public Optional<StoreItemDto> purchaseItem(UUID itemId, UUID studentId, String studentName) {
        if (studentId == null) {
            throw new AppException(
                    "Student id is required for purchase",
                    "Student id is required for purchase",
                    "INVALID_PURCHASE",
                    HttpStatus.BAD_REQUEST);
        }
        return storeItemRepository.findById(itemId).flatMap(item -> {
            ClaimWindowStatus window = resolveWindowStatus(item.getClaimOpensAt(), item.getClaimClosesAt());
            if (window == ClaimWindowStatus.NOT_OPEN) {
                throw new AppException(
                        "Claim window not open",
                        "This item is not open for claiming yet",
                        "CLAIM_NOT_OPEN",
                        HttpStatus.BAD_REQUEST);
            }
            if (window == ClaimWindowStatus.EXPIRED) {
                throw new AppException(
                        "Claim window expired",
                        "This item's claim window has expired",
                        "CLAIM_EXPIRED",
                        HttpStatus.BAD_REQUEST);
            }
            if (item.getStock() <= 0) {
                log.warn("Item with ID {} is out of stock", itemId);
                return Optional.empty();
            }

            item.setStock(item.getStock() - 1);
            StoreItem saved = storeItemRepository.save(item);

            storePurchaseRepository.save(StorePurchase.builder()
                    .studentId(studentId)
                    .studentName(studentName)
                    .itemId(itemId)
                    .itemName(saved.getName())
                    .build());

            log.info("Student {} claimed item {}; stock now {}", studentId, itemId, saved.getStock());
            return Optional.of(enrichWindowAndClaims(StoreItemMapper.toDto(saved)));
        });
    }

    public List<StorePurchaseDto> getPurchasesForStudent(UUID studentId) {
        return storePurchaseRepository.findByStudentIdOrderByClaimedAtDesc(studentId).stream()
                .map(this::toPurchaseDto)
                .toList();
    }

    public List<StorePurchaseDto> getAllPurchases() {
        return storePurchaseRepository.findAllByOrderByClaimedAtDesc().stream()
                .map(this::toPurchaseDto)
                .toList();
    }

    private StorePurchaseDto toPurchaseDto(StorePurchase p) {
        StorePurchaseDto.StorePurchaseDtoBuilder b = StorePurchaseDto.builder()
                .id(p.getId())
                .studentId(p.getStudentId())
                .studentName(p.getStudentName())
                .itemId(p.getItemId())
                .itemName(p.getItemName())
                .claimedAt(p.getClaimedAt());
        storeItemRepository.findById(p.getItemId()).ifPresent(item -> {
            b.acquisitionType(item.getAcquisitionType() != null ? item.getAcquisitionType().name() : null);
            b.itemCategory(item.getItemCategory() != null ? item.getItemCategory().name() : null);
        });
        return b.build();
    }

    private void validateWindows(StoreItemDto dto) {
        if (dto.getClaimOpensAt() != null && dto.getClaimClosesAt() != null
                && dto.getClaimClosesAt().isBefore(dto.getClaimOpensAt())) {
            throw new AppException(
                    "Claim close must be after claim open",
                    "Claim close time must be after open time",
                    "INVALID_ITEM",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void validateAcquisition(StoreItemDto dto) {
        AcquisitionType type = dto.getAcquisitionType() != null
                ? dto.getAcquisitionType()
                : AcquisitionType.POINTS;
        dto.setAcquisitionType(type);
        if (dto.getItemCategory() == null) {
            dto.setItemCategory(ItemCategory.MATERIAL);
        }
        if (dto.getItemCategory() == ItemCategory.OPPORTUNITY) {
            if (dto.getOpportunityBrief() == null || dto.getOpportunityBrief().isBlank()) {
                throw new AppException(
                        "Opportunity brief is required",
                        "Opportunities need a brief explaining why they exist",
                        "INVALID_ITEM",
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            dto.setOpportunityBrief(null);
            dto.setIntendedAudience(null);
            dto.setEligibilityChecklist(new ArrayList<>());
        }

        List<UUID> taskIds = dto.getRequiredTaskIds() != null
                ? dto.getRequiredTaskIds().stream().filter(id -> id != null).distinct().toList()
                : List.of();
        dto.setRequiredTaskIds(new ArrayList<>(taskIds));

        int price = dto.getPrice() != null ? dto.getPrice() : 0;

        switch (type) {
            case POINTS -> {
                if (price < 1) {
                    throw new AppException(
                            "Price must be at least 1 for points-only items",
                            "Price must be at least 1 for points-only items",
                            "INVALID_ITEM",
                            HttpStatus.BAD_REQUEST);
                }
                dto.setRequiredTaskIds(new ArrayList<>());
            }
            case TASKS -> {
                if (taskIds.isEmpty()) {
                    throw new AppException(
                            "Select at least one required task",
                            "Task-unlock items need at least one linked task",
                            "INVALID_ITEM",
                            HttpStatus.BAD_REQUEST);
                }
                if (price < 0) {
                    throw new AppException(
                            "Price cannot be negative",
                            "Price cannot be negative",
                            "INVALID_ITEM",
                            HttpStatus.BAD_REQUEST);
                }
            }
            case POINTS_AND_TASKS -> {
                if (taskIds.isEmpty()) {
                    throw new AppException(
                            "Select at least one required task",
                            "Items that need tasks and points require linked tasks",
                            "INVALID_ITEM",
                            HttpStatus.BAD_REQUEST);
                }
                if (price < 1) {
                    throw new AppException(
                            "Price must be at least 1 when points are required",
                            "Price must be at least 1 when points are required",
                            "INVALID_ITEM",
                            HttpStatus.BAD_REQUEST);
                }
            }
        }
    }
}
