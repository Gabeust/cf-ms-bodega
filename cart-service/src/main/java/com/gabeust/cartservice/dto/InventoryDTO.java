package com.gabeust.cartservice.dto;

public record InventoryDTO(Long wineId, int quantity, WineDTO wineDTO) {}