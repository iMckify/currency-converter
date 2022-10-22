package com.lbapp.LBcalc.repos;

import com.lbapp.LBcalc.currency.models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepo extends JpaRepository<Currency, Integer> {
    Optional<Currency> findBySymbolContains(String symbol);
}
