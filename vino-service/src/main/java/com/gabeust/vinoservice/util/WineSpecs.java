package com.gabeust.vinoservice.util;

import com.gabeust.vinoservice.entity.Wine;
import org.springframework.data.jpa.domain.Specification;
/**
 * Clase utilitaria que contiene métodos estáticos para construir
 * {@link Specification} dinámicas para la entidad {@link Wine}.
 *
 * Permite aplicar filtros por nombre, bodega, varietal, año y precio.
 */
public class WineSpecs {

    /**
     * Filtra vinos cuyo nombre contenga el valor dado (sin importar mayúsculas o minúsculas).
     *
     * @param name nombre parcial a buscar
     * @return especificación para coincidencia por nombre (LIKE)
     */
    public static Specification<Wine> nameLike(String name) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
    /**
     * Filtra vinos cuya bodega contenga el valor dado (sin importar mayúsculas o minúsculas).
     *
     * @param winery nombre de bodega parcial a buscar
     * @return especificación para coincidencia por bodega (LIKE)
     */
    public static Specification<Wine> wineryLike(String winery) {
        return (root, query, builder)
                -> builder.like(builder.lower(root.get("winery")), "%" + winery.toLowerCase() + "%");
    }
    /**
     * Filtra vinos cuyo varietal contenga el valor dado (sin importar mayúsculas o minúsculas).
     *
     * @param varietal nombre parcial del varietal a buscar
     * @return especificación para coincidencia por varietal (LIKE)
     */
    public static Specification<Wine> varietalLike(String varietal) {
        return (root, query, builder) -> builder.like(builder.lower(root.get("varietal")), "%" + varietal.toLowerCase() + "%");
    }
    /**
     * Filtra vinos cuyo año coincida exactamente con el valor especificado.
     *
     * @param year año exacto a buscar
     * @return especificación para coincidencia exacta de año
     */
    public static Specification<Wine> yearEquals(Integer year) {
        return (root, query, builder) -> builder.equal(root.get("year"), year);
    }
    /**
     * Filtra vinos cuyo año sea mayor o igual al valor especificado.
     *
     * @param yearMin año mínimo
     * @return especificación para año >= yearMin
     */
    public static Specification<Wine> yearGte(Integer yearMin) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("year"), yearMin);
    }
    /**
     * Filtra vinos cuyo año sea menor o igual al valor especificado.
     *
     * @param yearMax año máximo
     * @return especificación para año <= yearMax
     */
    public static Specification<Wine> yearLte(Integer yearMax) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("year"), yearMax);
    }
    /**
     * Filtra vinos cuyo precio coincida exactamente con el valor especificado.
     *
     * @param price precio exacto
     * @return especificación para coincidencia exacta de precio
     */
    public static Specification<Wine> priceEquals(Integer price) {
        return (root, query, builder) -> builder.equal(root.get("price"), price);
    }
    /**
     * Filtra vinos cuyo precio sea mayor o igual al valor especificado.
     *
     * @param priceMin precio mínimo
     * @return especificación para precio >= priceMin
     */
    public static Specification<Wine> priceGte(Integer priceMin) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), priceMin);
    }
    /**
     * Filtra vinos cuyo precio sea menor o igual al valor especificado.
     *
     * @param priceMax precio máximo
     * @return especificación para precio <= priceMax
     */
    public static Specification<Wine> priceLte(Integer priceMax) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), priceMax);
    }
}
