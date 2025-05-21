package com.gabeust.inventoryservice.service;

import com.gabeust.inventoryservice.dto.InventoryWithWineDTO;
import com.gabeust.inventoryservice.dto.WineDTO;
import com.gabeust.inventoryservice.entity.Inventory;
import com.gabeust.inventoryservice.entity.enums.MovementType;
import com.gabeust.inventoryservice.repository.InventoryRepository;
import com.gabeust.inventoryservice.service.client.WineClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WineClientService wineClientService;
    private final InventoryMovementService movementService;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, WineClientService wineClientService, InventoryMovementService movementService) {
        this.inventoryRepository = inventoryRepository;
        this.wineClientService = wineClientService;
        this.movementService = movementService;
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
        Optional<Inventory> existing = inventoryRepository.findByWineId(inventory.getWineId());
        if (existing.isPresent()) {
            Inventory current = existing.get();
            current.setQuantity(current.getQuantity() + inventory.getQuantity());
            return inventoryRepository.save(current);
        } else {
            return inventoryRepository.save(inventory);
        }
    }

    @Override
    public void increaseStock(Long wineId, int amount) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        inventory.setQuantity(inventory.getQuantity() + amount);
        inventoryRepository.save(inventory);
        movementService.registerMovement(wineId, MovementType.INCREASE, amount);
    }

    @Override
    public void decreaseStock(Long wineId, int amount) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        int newQuantity = inventory.getQuantity() - amount;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock for wine with ID: " + wineId);
        }
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
        movementService.registerMovement(wineId, MovementType.DECREASE, amount);
    }

    @Override
    public boolean deleteByWineId(Long wineId) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        inventoryRepository.delete(inventory);
        return true;
    }
    public InventoryWithWineDTO findInventoryWithWineInfo(Long wineId) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine ID: " + wineId));

        WineDTO wine = wineClientService.getWineById(wineId);

        return new InventoryWithWineDTO(
                wineId,
                inventory.getQuantity(),
                inventory.getMinimumQuantity(),
                wine
        );
    }
}
