package com.gabeust.inventoryservice.service;

import com.gabeust.inventoryservice.dto.InventoryWithWineDTO;
import com.gabeust.inventoryservice.dto.WineDTO;
import com.gabeust.inventoryservice.entity.Inventory;
import com.gabeust.inventoryservice.entity.enums.MovementType;
import com.gabeust.inventoryservice.repository.InventoryRepository;
import com.gabeust.inventoryservice.service.client.WineClientService;
import com.gabeust.inventoryservice.service.messaging.producer.StockAlertProducer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio de inventario que gestiona las operaciones sobre el inventario de vinos.
 *
 * Este servicio permite buscar, crear, actualizar, eliminar inventarios y manejar movimientos de stock.
 * También integra llamadas al servicio de vinos para validar la existencia de los vinos asociados.
 * Además, emite alertas mediante Kafka cuando el stock alcanza niveles mínimos.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WineClientService wineClientService;
    private final InventoryMovementService movementService;
    private final StockAlertProducer stockAlertProducer;  // <-- Agregado

    public InventoryServiceImpl(InventoryRepository inventoryRepository, WineClientService wineClientService, InventoryMovementService movementService, StockAlertProducer stockAlertProducer) {
        this.inventoryRepository = inventoryRepository;
        this.wineClientService = wineClientService;
        this.movementService = movementService;
        this.stockAlertProducer = stockAlertProducer;
    }
    /**
     * Obtiene todos los inventarios registrados.
     *
     * @return lista de todos los inventarios
     */
    @Override
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }
    /**
     * Busca el inventario correspondiente a un vino específico.
     *
     * @param wineId ID del vino
     * @return Optional con el inventario si existe
     */
    @Override
    public Optional<Inventory> findByWineId(Long wineId) {
        return inventoryRepository.findByWineId(wineId);
    }
    /**
     * Guarda un nuevo inventario validando que el vino exista y que no exista inventario previo para ese vino.
     * Además, registra un movimiento inicial si la cantidad es mayor a cero.
     *
     * @param inventory inventario a guardar
     * @return inventario guardado
     * @throws IllegalArgumentException si el vino no existe
     * @throws IllegalStateException    si ya existe inventario para ese vino o hay error en la validación
     */
    @Override
    public Inventory save(Inventory inventory) {
        try {
            wineClientService.getWineById(inventory.getWineId());
        } catch (WebClientResponseException.NotFound e) {
            throw new IllegalArgumentException("Wine with ID " + inventory.getWineId() + " does not exist.");
        } catch (Exception e) {
            throw new IllegalStateException("Error while verifying the wine: " + e.getMessage(), e);
        }

        // Validar que no exista inventario para ese vino
        if (inventoryRepository.existsByWineId(inventory.getWineId())) {
            throw new IllegalStateException("An inventory already exists for the wine with ID: " + inventory.getWineId());
        }

        // Guardar inventario
        Inventory savedInventory = inventoryRepository.save(inventory);

        // Registrar movimiento solo si la cantidad es mayor a 0
        if (savedInventory.getQuantity() > 0) {
            movementService.registerMovement(
                    savedInventory.getWineId(),
                    MovementType.INCREASE,
                    savedInventory.getQuantity()
            );
        }

        return savedInventory;
    }
    /**
     * Incrementa el stock de un vino en la cantidad especificada y registra el movimiento.
     *
     * @param wineId ID del vino
     * @param amount cantidad a incrementar
     * @throws RuntimeException si no se encuentra el inventario para el vino
     */
    @Override
    public void increaseStock(Long wineId, int amount) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        inventory.setQuantity(inventory.getQuantity() + amount);
        inventoryRepository.save(inventory);
        movementService.registerMovement(wineId, MovementType.INCREASE, amount);
    }
    /**
     * Decrementa el stock de un vino en la cantidad especificada y registra el movimiento.
     * Envía una alerta si el nuevo stock es menor o igual al stock mínimo permitido.
     *
     * @param wineId ID del vino
     * @param amount cantidad a decrementar
     * @throws RuntimeException si no se encuentra inventario o si el stock es insuficiente
     */
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
        if (newQuantity <= inventory.getMinimumQuantity()) {
            stockAlertProducer.sendStockAlert(wineId.toString());
        }
    }
    /**
     * Elimina el inventario asociado a un vino específico.
     *
     * @param wineId ID del vino
     * @return true si la eliminación fue exitosa
     * @throws RuntimeException si no se encuentra el inventario
     */
    @Override
    public boolean deleteByWineId(Long wineId) {
        Inventory inventory = inventoryRepository.findByWineId(wineId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for wine with ID: " + wineId));

        inventoryRepository.delete(inventory);
        return true;
    }
    /**
     * Obtiene un DTO que combina la información del inventario con los datos del vino consultados mediante el servicio de vinos.
     *
     * @param wineId ID del vino
     * @return DTO con inventario y datos del vino
     * @throws RuntimeException si no se encuentra el inventario
     */
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
