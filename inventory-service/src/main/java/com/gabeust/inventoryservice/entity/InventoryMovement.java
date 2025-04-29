package com.gabeust.inventoryservice.entity;

import com.gabeust.inventoryservice.entity.enums.MovementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long wineId;
    @Enumerated(EnumType.STRING)
    private MovementType type;
    private Integer quantity;
    private LocalDate date;

    @PrePersist
    public void prePersist() {
        date = LocalDate.now();
    }

}
