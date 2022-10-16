package com.lbapp.LBcalc.adapters;

import com.lbapp.LBcalc.services.ForexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.HOURS;

@Slf4j
@Component
public class ForexAdapter {
    private final ForexService forexService;

    public ForexAdapter(ForexService forexService) {
        this.forexService = forexService;
    }

    @Scheduled(timeUnit = HOURS, fixedDelay = 24)
    private void updateFxRates() {
        log.info("Executing scheduled task {}()", new Object() {
        }.getClass().getEnclosingMethod().getName());

        this.forexService.updateCurrenciesWithLiveExchangeRates();
    }
}
