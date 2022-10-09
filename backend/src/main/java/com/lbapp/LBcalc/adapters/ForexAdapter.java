package com.lbapp.LBcalc.adapters;

import com.lbapp.LBcalc.services.ForexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ForexAdapter {
    private final ForexService forexService;

    public ForexAdapter(ForexService forexService) {
        this.forexService = forexService;
    }

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    private void updateFxRates() {
        log.info("Executing scheduled task {}()", new Object() {
        }.getClass().getEnclosingMethod().getName());

        this.forexService.updateFxRates();
    }
}
