package com.gabeust.inventoryservice.dto;

public record InventoryWithWineDTO(
        Long wineId,
        Integer quantity,
        Integer minimumQuantity,
        WineDTO wine
) {
}
