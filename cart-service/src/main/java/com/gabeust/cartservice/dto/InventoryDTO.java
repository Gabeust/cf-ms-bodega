package com.gabeust.cartservice.dto;
/**
 * DTO que representa el inventario de un vino junto con información detallada del vino.
 *
 * @param wineId   Identificador del vino.
 * @param quantity Cantidad disponible en el inventario.
 * @param wineDTO  Información detallada del vino.
 */
public record InventoryDTO(Long wineId, int quantity, WineDTO wineDTO) {}