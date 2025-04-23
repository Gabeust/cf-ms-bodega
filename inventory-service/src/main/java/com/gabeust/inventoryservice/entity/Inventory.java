package com.gabeust.inventoryservice.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Inventory {

    private Long id;
    private Long wineId;
    private Integer quantity;
    private Integer minimumQuantity;
    private LocalDateTime updatedAt;


}
