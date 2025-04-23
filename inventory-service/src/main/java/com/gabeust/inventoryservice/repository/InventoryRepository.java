package com.gabeust.inventoryservice.repository;

import com.gabeust.inventoryservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByWineId(Long wineId);
    boolean existsByWineId();
}
