package com.lbapp.LBcalc.repos;

import com.lbapp.LBcalc.models.CurrentFxRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentFxRatesRepo extends JpaRepository<CurrentFxRate, Integer> {
}
