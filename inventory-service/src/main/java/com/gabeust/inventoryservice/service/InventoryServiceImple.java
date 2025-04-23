package com.gabeust.inventoryservice.service;

import com.gabeust.inventoryservice.entity.Inventory;
import com.gabeust.inventoryservice.repository.InventoryRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryServiceImple {

    List<Inventory> findAll();
    Optional<Inventory> findByWineId(Long wineId);
    Inventory save(Inventory inventory);
    void increaseStock(Long wineId, int amount);
    void decreaseStock(Long wineId, int amount);
    boolean deleteByWineId(Long wineId);

}
