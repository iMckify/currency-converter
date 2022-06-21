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

//    @Scheduled(cron = "0 0/3 15-22 ? * MON-FRI", zone = "Europe/Vilnius") // every 3 minutes, from 15:00 to 22:00, work days
    @Scheduled(cron = "*/20 * * ? * MON-FRI", zone = "Europe/Vilnius") // every 3 minutes, from 15:00 to 22:00, work days
    private void updateFxRates() {
        logger.info("Executing scheduled task {}()", new Object(){}.getClass().getEnclosingMethod().getName());

        this.forexService.updateFxRates();
    }
}
