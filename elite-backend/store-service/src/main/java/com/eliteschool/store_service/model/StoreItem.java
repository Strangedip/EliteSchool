package com.eliteschool.store_service.model;

import jakarta.persistence.*;
import lombok.*;

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
    private String name; // Item name (e.g., "Notebook", "Library Pass")

    @Column(nullable = false)
    private int price; // Points required to redeem the item

    @Column(nullable = false)
    private int stock; // Available stock quantity

    private String description; // Optional item description
    
    private String imageUrl; // Optional image URL for the item
}
