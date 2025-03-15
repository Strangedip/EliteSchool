package com.eliteschool.reward_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "redeemable_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedeemableItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // Item name (e.g., "Notebook", "Library Pass")

    @Column(nullable = false)
    private int price; // Points required to redeem the item

    @Column(nullable = false)
    private int stock; // Available stock quantity
}
