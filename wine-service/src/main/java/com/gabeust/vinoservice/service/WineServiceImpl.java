package com.gabeust.vinoservice.service;

import com.gabeust.vinoservice.entity.Wine;
import com.gabeust.vinoservice.repository.WineRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/**
 * Implementación del servicio para la gestión de vinos.
 *
 * Provee métodos para operaciones CRUD y búsquedas avanzadas utilizando Specifications.
 * Interactúa con el repositorio de vinos para persistir y recuperar datos.
 */
@Service
public class WineServiceImpl implements WineService {

    private final WineRepository wineRepository;

    public WineServiceImpl(WineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }
    /**
     * Recupera todos los vinos disponibles en el sistema.
     *
     * @return Lista de todos los vinos.
     */
    @Override
    public List<Wine> findAll() {
        return wineRepository.findAll();
    }
    /**
     * Busca vinos que cumplan con las condiciones definidas en la Specification.
     *
     * @param spec Especificación con los criterios de búsqueda.
     * @return Lista de vinos que cumplen la especificación.
     */
    public List<Wine> searchWithSpecification(Specification<Wine> spec) {
        return wineRepository.findAll(spec);
    }
    /**
     * Busca un vino por su identificador.
     *
     * @param id Identificador único del vino.
     * @return Optional que contiene el vino si se encuentra, o vacío si no.
     */
    @Override
    public Optional<Wine> findById(Long id) {
        return wineRepository.findById(id);
    }
    /**
     * Guarda un nuevo vino en el sistema o actualiza uno existente.
     *
     * @param wine Objeto vino a guardar.
     * @return Vino guardado con su ID asignado.
     */
    @Override
    public Wine save(Wine wine) {
        return wineRepository.save(wine);
    }

    /**
     * Elimina un vino por su ID.
     *
     * @param id Identificador del vino a eliminar.
     * @return true si se eliminó con éxito, false si no existía el vino.
     */
    @Override
    public boolean deleteById(long id) {
        if (wineRepository.existsById(id)) {
            wineRepository.deleteById(id);
            return true;
        }
        return false;

    }
    /**
     * Actualiza los datos de un vino existente identificado por su ID.
     *
     * @param id Identificador del vino a actualizar.
     * @param newWine Objeto con los datos nuevos para actualizar.
     * @return El vino actualizado.
     * @throws RuntimeException Si no se encuentra el vino con el ID dado.
     */
    @Override
    public Wine updateById(Long id, Wine newWine) {
        return findById(id).map(existingWine -> {
            existingWine.setName(newWine.getName());
            existingWine.setWinery(newWine.getWinery());
            existingWine.setVarietal(newWine.getVarietal());
            existingWine.setYear(newWine.getYear());
            existingWine.setDescription(newWine.getDescription());
            existingWine.setPrice(newWine.getPrice());
            return wineRepository.save(existingWine);
        }).orElseThrow(() -> new RuntimeException("Wine not found with ID: " + id));
    }
}
