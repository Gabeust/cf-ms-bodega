package com.gabeust.inventoryservice.service;

import com.gabeust.inventoryservice.entity.Inventory;
import com.gabeust.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Optional<Inventory> findByWineId(Long wineId) {
        return inventoryRepository.findByWineId(wineId);
    }

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public void increaseStock(Long wineId, int amount) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: "+ wineId));

        inventory.setQuantity(inventory.getQuantity() + amount);
        inventoryRepository.save(inventory);
    }

    @Override
    public void decreaseStock(Long wineId, int amount) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        int newQuantity = inventory.getQuantity() - amount;
        if (newQuantity < 0) {
            newQuantity = 0;
        }
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public boolean deleteByWineId(Long wineId) {
        if (inventoryRepository.existsByWineId(wineId)){
            inventoryRepository.deleteById(wineId);
            return true;
        }else return false;
    }
}
