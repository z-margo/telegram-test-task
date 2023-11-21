package com.example.demo.repo;

import com.example.demo.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Optional<Currency> findByTimeIs(LocalTime time);

    List<Currency> findByTimeAfter(LocalTime time);
}
