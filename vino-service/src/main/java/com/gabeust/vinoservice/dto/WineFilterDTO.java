package com.gabeust.vinoservice.dto;

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
