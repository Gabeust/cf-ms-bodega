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

@RestController
@RequestMapping("/api/v1/wines")
public class WineController {

    private final WineService wineService;

    public WineController(WineService wineService) {
        this.wineService = wineService;
    }

    @GetMapping("/filter")
    public List<Wine> filterWines(@ParameterObject WineFilterDTO filters) {
        Specification<Wine> spec = Specification.where(null);

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
        return wineService.searchWithSpecification(spec);

    }

    @GetMapping()
    public List<Wine> findAll() {
        return wineService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wine> findById(@PathVariable Long id) {
        return wineService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public URI create(@RequestBody Wine wine, UriComponentsBuilder ucb) {
        wineService.save(wine);
        URI wineUri = ucb.path("wines/{id}")
                .buildAndExpand(wine.getId()).toUri();
        return wineUri;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wine> updateWine(@RequestBody Wine newWine, @PathVariable Long id) {
        Wine updated = wineService.updateById(id, newWine);
        return ResponseEntity.ok(updated);
    }
}