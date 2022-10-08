package com.lbapp.LBcalc.adapters;

import com.lbapp.LBcalc.services.ForexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ForexAdapter {
    public static final Logger logger = LoggerFactory.getLogger(ForexAdapter.class);

    private final ForexService forexService;

    public ForexAdapter(ForexService forexService) {
        this.forexService = forexService;
    }

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    private void updateFxRates() {
        logger.info("Executing scheduled task {}()", new Object() {
        }.getClass().getEnclosingMethod().getName());

        this.forexService.updateFxRates();
    }
}
