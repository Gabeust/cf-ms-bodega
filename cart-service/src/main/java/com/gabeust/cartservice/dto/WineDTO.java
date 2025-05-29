package com.gabeust.cartservice.dto;
/**
 * DTO que representa la información básica de un vino.
 *
 * @param id      Identificador único del vino.
 * @param name    Nombre del vino.
 * @param winery  Bodega que produce el vino.
 * @param price   Precio del vino.
 */
public record WineDTO(Long id, String name, String winery, double price) {}
