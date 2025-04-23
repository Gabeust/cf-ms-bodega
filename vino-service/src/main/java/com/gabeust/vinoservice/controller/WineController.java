package com.gabeust.vinoservice.controller;

import com.gabeust.vinoservice.dto.WineFilterDTO;
import com.gabeust.vinoservice.entity.Wine;
import com.gabeust.vinoservice.service.WineService;
import com.gabeust.vinoservice.util.WineSpecs;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
/**
 * Controlador REST para gestionar vinos.
 * Permite operaciones de CRUD y búsqueda avanzada con filtros.
 */
@RestController
@RequestMapping("/api/v1/wines")
public class WineController {

    private final WineService wineService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param wineService servicio de vinos
     */
    public WineController(WineService wineService) {
        this.wineService = wineService;
    }
    /**
     * Filtra vinos según múltiples criterios opcionales como nombre, bodega,
     * varietal, año, y precio (exacto o por rangos).
     *
     * @param filters objeto con los filtros de búsqueda enviados como parámetros
     * @return lista de vinos que cumplen con los criterios
     */
    @GetMapping("/filter")
    public List<Wine> filterWines(@ParameterObject WineFilterDTO filters) {
        // Inicializa una especificación vacía
        Specification<Wine> spec = Specification.where(null);
        // Aplica filtros si fueron provistos
        if (filters.name() != null)
            spec = spec.and(WineSpecs.nameLike(filters.name()));
        if (filters.winery() != null)
            spec = spec.and(WineSpecs.wineryLike(filters.winery()));
        if (filters.varietal() != null)
            spec = spec.and(WineSpecs.varietalLike(filters.varietal()));
        if (filters.year() != null)
            spec = spec.and(WineSpecs.yearEquals(filters.year()));
        if (filters.yearMin() != null)
            spec = spec.and(WineSpecs.yearGte(filters.yearMin()));
        if (filters.yearMax() != null)
            spec = spec.and(WineSpecs.yearLte(filters.yearMax()));
        if (filters.price() != null)
            spec = spec.and(WineSpecs.priceEquals(filters.price()));
        if (filters.priceMin() != null)
            spec = spec.and(WineSpecs.priceGte(filters.priceMin()));
        if (filters.priceMax() != null)
            spec = spec.and(WineSpecs.priceLte(filters.priceMax()));
        // Ejecuta la búsqueda con la especificación compuesta
        return wineService.searchWithSpecification(spec);

    }
    /**
     * Obtiene la lista completa de vinos.
     *
     * @return lista de todos los vinos
     */
    @GetMapping()
    public List<Wine> findAll() {
        return wineService.findAll();
    }
    /**
     * Busca un vino por su ID.
     *
     * @param id ID del vino a buscar
     * @return ResponseEntity con el vino si se encuentra, o 404 si no
     */
    @GetMapping("/{id}")
    public ResponseEntity<Wine> findById(@PathVariable Long id) {
        return wineService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * Crea un nuevo vino.
     *
     * @param wine objeto Vino a guardar
     * @param ucb  UriComponentsBuilder para construir la URI de respuesta
     * @return URI del recurso creado
     */
    @PostMapping()
    public URI create(@RequestBody Wine wine, UriComponentsBuilder ucb) {
        wineService.save(wine);
        URI wineUri = ucb.path("wines/{id}")
                .buildAndExpand(wine.getId()).toUri();
        return wineUri;
    }
    /**
     * Actualiza un vino existente por ID.
     *
     * @param newWine objeto con los nuevos datos del vino
     * @param id      ID del vino a actualizar
     * @return ResponseEntity con el vino actualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Wine> updateWine(@RequestBody Wine newWine, @PathVariable Long id) {
        Wine updated = wineService.updateById(id, newWine);
        return ResponseEntity.ok(updated);
    }
    /**
     * Elimina un vino existente por su ID.
     *
     * Si no se encuentra un vino con el ID proporcionado, se devuelve una respuesta 404 Not Found.
     * Si la eliminación es exitosa, se devuelve una respuesta 204 No Content.
     *
     * @param id ID del vino que se desea eliminar.
     * @return ResponseEntity con el código de estado correspondiente.
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteByid(@PathVariable Long id){
        boolean deleted = wineService.deleteById(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
    }
}