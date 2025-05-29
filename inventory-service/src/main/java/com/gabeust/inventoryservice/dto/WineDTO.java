package com.gabeust.inventoryservice.dto;
/**
 * DTO que representa los datos básicos de un vino.
 *
 * Este objeto se utiliza para transferir información simplificada del vino entre servicios o hacia el cliente.
 *
 * @param name nombre del vino
 * @param winery nombre de la bodega productora
 * @param year año de producción del vino
 * @param price precio actual del vino
 */
public record WineDTO(String name, String winery, Integer year, double price) {}
