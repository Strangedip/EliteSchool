package com.eliteschool.store_service.model;

import com.eliteschool.store_service.model.enums.AcquisitionType;
import com.eliteschool.store_service.model.enums.ItemCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "store_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    private String description;

    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String opportunityBrief;

    @Column(length = 500)
    private String intendedAudience;

    @ElementCollection
    @CollectionTable(name = "store_item_eligibility_checklist", joinColumns = @JoinColumn(name = "store_item_id"))
    @Column(name = "checklist_item", length = 500)
    @OrderColumn(name = "item_order")
    @Builder.Default
    private List<String> eligibilityChecklist = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AcquisitionType acquisitionType = AcquisitionType.POINTS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ItemCategory itemCategory = ItemCategory.MATERIAL;

    private LocalDateTime claimOpensAt;

    private LocalDateTime claimClosesAt;

    @ElementCollection
    @CollectionTable(name = "store_item_required_tasks", joinColumns = @JoinColumn(name = "store_item_id"))
    @Column(name = "task_id")
    @Builder.Default
    private List<UUID> requiredTaskIds = new ArrayList<>();
}
