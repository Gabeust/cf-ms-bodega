package com.gabeust.inventoryservice.service;

import com.gabeust.inventoryservice.entity.InventoryMovement;
import com.gabeust.inventoryservice.entity.enums.MovementType;
import com.gabeust.inventoryservice.repository.InventoryMovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Servicio para gestionar los movimientos de inventario relacionados con los vinos.
 *
 * Permite registrar nuevos movimientos de inventario y consultar movimientos existentes
 * por ID de vino.
 */
@Service
public class InventoryMovementService {
    private final InventoryMovementRepository movementRepository;
    /**
     * Constructor que inyecta el repositorio de movimientos de inventario.
     *
     * @param movementRepository repositorio para acceder a los movimientos de inventario
     */
    public InventoryMovementService(InventoryMovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }
    /**
     * Registra un nuevo movimiento de inventario para un vino específico.
     *
     * @param wineId   ID del vino
     * @param type     tipo de movimiento (ej. entrada, salida)
     * @param quantity cantidad del movimiento
     */
    public void registerMovement(Long wineId, MovementType type, int quantity) {
        InventoryMovement movement = new InventoryMovement();
        movement.setWineId(wineId);
        movement.setType(type);
        movement.setQuantity(quantity);
        movementRepository.save(movement);
    }
    /**
     * Obtiene la lista de movimientos de inventario para un vino específico.
     *
     * @param wineId ID del vino
     * @return lista de movimientos de inventario relacionados con el vino
     */
    public List<InventoryMovement> findByWineId(Long wineId) {
        return movementRepository.findByWineId(wineId);
    }
}
