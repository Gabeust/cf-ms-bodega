package com.gabeust.vinoservice.service;

import com.gabeust.vinoservice.entity.Wine;
import com.gabeust.vinoservice.repository.WineRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WineService implements WineServiceImpl{

    private final WineRepository wineRepository;

    public WineService(WineRepository wineRepository) {
        this.wineRepository = wineRepository;
    }

    @Override
    public List<Wine> findAll() {
        return wineRepository.findAll();
    }
    public List<Wine> searchWithSpecification(Specification<Wine> spec) {
        return wineRepository.findAll(spec);
    }

    @Override
    public Optional<Wine> findById(Long id) {
        return wineRepository.findById(id);
    }

    @Override
    public Wine save(Wine wine) {
        return wineRepository.save(wine);
    }

    @Override
    public void deleteById(long id) {
        wineRepository.deleteById(id);
    }

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
