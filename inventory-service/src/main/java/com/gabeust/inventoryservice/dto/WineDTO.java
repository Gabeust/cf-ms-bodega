package com.gabeust.inventoryservice.dto;

public record WineDTO(Long id, String name, String winery, Integer year, double price) {}
