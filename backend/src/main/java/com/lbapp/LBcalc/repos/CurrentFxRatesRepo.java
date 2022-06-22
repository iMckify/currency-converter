package com.lbapp.LBcalc.repos;

import com.lbapp.LBcalc.models.CurrentFxRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrentFxRatesRepo extends JpaRepository<CurrentFxRate, Integer> {
    Optional<CurrentFxRate> findBySymbolContains (String symbol);
}
