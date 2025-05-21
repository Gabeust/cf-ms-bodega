package com.gabeust.inventoryservice.controller;

import com.gabeust.inventoryservice.dto.InventoryWithWineDTO;
import com.gabeust.inventoryservice.entity.Inventory;
import com.gabeust.inventoryservice.entity.enums.MovementType;
import com.gabeust.inventoryservice.service.InventoryMovementService;
import com.gabeust.inventoryservice.service.InventoryServiceImpl;
import com.gabeust.inventoryservice.service.client.WineClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryServiceImpl inventoryService;
    private final InventoryMovementService movementService;
    private final WineClientService wineClientService;
    public InventoryController(InventoryServiceImpl inventoryService, InventoryMovementService movementService, WineClientService wineClientService) {
        this.inventoryService = inventoryService;
        this.movementService = movementService;
        this.wineClientService = wineClientService;
    }

    /**
     * Obtiene todos los registros de inventario.
     */
    @GetMapping
    public List<Inventory> findAll() {
        return inventoryService.findAll();
    }

    /**
     * Obtiene el inventario de un vino por su ID.
     *
     * @param wineId ID del vino
     * @return Inventario asociado al vino
     */
    @GetMapping("/details/{wineId}")
    public ResponseEntity<InventoryWithWineDTO> findByWineId(@PathVariable Long wineId) {
        InventoryWithWineDTO dto = inventoryService.findInventoryWithWineInfo(wineId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Crea o actualiza un inventario.
     *
     * @param inventory objeto inventario
     * @return Inventario guardado
     */
    @PostMapping
    public ResponseEntity<Inventory> save(@RequestBody Inventory inventory) {
        try {
            Inventory saved = inventoryService.save(inventory);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    /**
     * Incrementa el stock de un vino.
     *
     * @param wineId ID del vino
     * @param amount cantidad a aumentar
     */
    @PostMapping("/{wineId}/increase")
    public ResponseEntity<Void> increaseStock(@PathVariable Long wineId, @RequestParam int amount) {
        inventoryService.increaseStock(wineId, amount);
        return ResponseEntity.ok().build();
    }

    /**
     * Disminuye el stock de un vino.
     *
     * @param wineId ID del vino
     * @param amount cantidad a disminuir
     */
    @PostMapping("/{wineId}/decrease")
    public ResponseEntity<Void> decreaseStock(@PathVariable Long wineId, @RequestParam int amount) {
        inventoryService.decreaseStock(wineId, amount);
        return ResponseEntity.ok().build();
    }


    /**
     * Elimina el inventario asociado a un vino por su ID.
     *
     * Si no se encuentra un inventario con el ID proporcionado, se devuelve una respuesta 404 Not Found.
     * Si la eliminación es exitosa, se devuelve una respuesta 204 No Content.
     *
     * @param wineId ID del vino que se desea eliminar.
     * @return ResponseEntity con el código de estado correspondiente.
     */
    @DeleteMapping("/{wineId}")
    public ResponseEntity<String> deleteByWineId(@PathVariable Long wineId) {
        boolean deleted = inventoryService.deleteByWineId(wineId);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
    }

}

