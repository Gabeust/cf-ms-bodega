package com.gabeust.notificationservice.repository;

import com.gabeust.notificationservice.entity.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
}
