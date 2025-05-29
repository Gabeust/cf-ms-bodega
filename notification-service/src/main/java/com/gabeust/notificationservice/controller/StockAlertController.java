package com.gabeust.notificationservice.controller;

import com.gabeust.notificationservice.entity.StockAlert;
import com.gabeust.notificationservice.repository.StockAlertRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/alerts")
public class StockAlertController {
    private final StockAlertRepository repository;

    public StockAlertController(StockAlertRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<StockAlert> getAllAlerts() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
}

