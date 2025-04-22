package com.gabeust.vinoservice.service;

import com.gabeust.vinoservice.entity.Wine;

import java.util.List;
import java.util.Optional;

public interface WineServiceImpl {

    List<Wine> findAll();

    Optional<Wine> findById(Long id);
    Wine save(Wine wine);
    void deleteById(long id);
    Wine updateById(Long id, Wine newWine);

}
