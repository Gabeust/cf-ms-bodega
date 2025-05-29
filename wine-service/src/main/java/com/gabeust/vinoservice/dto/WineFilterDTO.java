package com.gabeust.vinoservice.dto;
/**
 * DTO utilizado para filtrar vinos en función de distintos atributos.
 *
 * Permite aplicar filtros por nombre, bodega, varietal, año y precio,
 * incluyendo rangos mínimo y máximo para año y precio.
 *
 * Este DTO es útil para construir consultas dinámicas con múltiples criterios.
 *
 * @param name      nombre del vino.
 * @param winery    nombre de la bodega.
 * @param varietal  tipo de uva o mezcla (varietal).
 * @param year      año exacto del vino.
 * @param yearMin   año mínimo del rango de búsqueda.
 * @param yearMax   año máximo del rango de búsqueda.
 * @param price     precio exacto.
 * @param priceMin  precio mínimo del rango de búsqueda.
 * @param priceMax  precio máximo del rango de búsqueda.
 */
public record WineFilterDTO(
        String name,
        String winery,
        String varietal,
        Integer year,
        Integer yearMin,
        Integer yearMax,
        Integer price,
        Integer priceMin,
        Integer priceMax
) {
}
