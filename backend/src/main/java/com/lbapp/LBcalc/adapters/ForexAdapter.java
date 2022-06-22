package com.lbapp.LBcalc.adapters;

import com.lbapp.LBcalc.services.ForexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ForexAdapter {
    public static final Logger logger = LoggerFactory.getLogger(ForexAdapter.class);

    @Autowired
    private ForexService forexService;

//    @Scheduled(cron = "@daily") // every midnight
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000) // daily starting now
    private void updateFxRates() {
        logger.info("Executing scheduled task {}()", new Object(){}.getClass().getEnclosingMethod().getName());

        this.forexService.updateFxRates();
    }
}
