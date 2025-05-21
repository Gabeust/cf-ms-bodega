package com.gabeust.inventoryservice.service;

import com.gabeust.inventoryservice.entity.InventoryMovement;
import com.gabeust.inventoryservice.entity.enums.MovementType;
import com.gabeust.inventoryservice.repository.InventoryMovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryMovementService {
    private final InventoryMovementRepository movementRepository;

    public InventoryMovementService(InventoryMovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }
    public void registerMovement(Long wineId, MovementType type, int quantity) {
        InventoryMovement movement = new InventoryMovement();
        movement.setWineId(wineId);
        movement.setType(type);
        movement.setQuantity(quantity);
        movementRepository.save(movement);
    }

    public List<InventoryMovement> findByWineId(Long wineId) {
        return movementRepository.findByWineId(wineId);
    }
}
