package com.gabeust.inventoryservice.controller;

import com.gabeust.inventoryservice.entity.InventoryMovement;
import com.gabeust.inventoryservice.service.InventoryMovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/inventory/movements")
public class InventoryMovementController {
    private final InventoryMovementService movementService;

    public InventoryMovementController(InventoryMovementService movementService) {
        this.movementService = movementService;
    }

    @GetMapping("/{wineId}")
    public ResponseEntity<List<InventoryMovement>> getMovements(@PathVariable Long wineId) {
        return ResponseEntity.ok(movementService.findByWineId(wineId));
    }
}
