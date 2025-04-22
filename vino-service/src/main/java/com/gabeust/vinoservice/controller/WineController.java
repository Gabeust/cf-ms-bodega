package com.gabeust.vinoservice.controller;

import com.gabeust.vinoservice.entity.Wine;
import com.gabeust.vinoservice.service.WineService;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.GreaterThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LessThanOrEqual;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
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
    public List<Wine> searchWithFilters(
            @And({
                    @Spec(path = "name", params = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "winery", params = "winery", spec = LikeIgnoreCase.class),
                    @Spec(path = "varietal", params = "varietal", spec = LikeIgnoreCase.class),
                    @Spec(path = "year", params = "year", spec = Equal.class),
                    @Spec(path = "year", params = "yearMin", spec = GreaterThanOrEqual.class),
                    @Spec(path = "year", params = "yearMax", spec = LessThanOrEqual.class),
                    @Spec(path = "price", params = "price", spec = Equal.class),
                    @Spec(path = "price", params = "priceMin", spec = GreaterThanOrEqual.class),
                    @Spec(path = "price", params = "priceMax", spec = LessThanOrEqual.class)
            }) Specification<Wine> spec
    ) {
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