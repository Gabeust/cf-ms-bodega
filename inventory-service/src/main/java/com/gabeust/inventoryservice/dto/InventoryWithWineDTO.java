package com.gabeust.inventoryservice.dto;
/**
 * DTO que representa un inventario junto con la información del vino correspondiente.
 *
 * Este objeto combina la cantidad disponible y mínima de inventario con los detalles del vino.
 *
 * @param wineId ID del vino relacionado con este inventario
 * @param quantity cantidad actual disponible en el inventario
 * @param minimumQuantity cantidad mínima que debe mantenerse en inventario para este vino
 * @param wineDTO objeto que contiene la información detallada del vino
 */
public record InventoryWithWineDTO(
        Long wineId,
        Integer quantity,
        Integer minimumQuantity,
        WineDTO wineDTO
) {
}
