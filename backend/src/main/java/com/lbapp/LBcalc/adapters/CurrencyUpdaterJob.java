package com.lbapp.LBcalc.adapters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.HOURS;

@Slf4j
@Component
public class CurrencyUpdaterJob {

    private final CurrencyUpdaterService currencyUpdaterService;

    public CurrencyUpdaterJob(CurrencyUpdaterService currencyUpdaterService) {
        this.currencyUpdaterService = currencyUpdaterService;
    }

    @Scheduled(timeUnit = HOURS, fixedDelay = 24)
    private void updateFxRates() {
        log.info("Executing scheduled task {}()", new Object() {
        }.getClass().getEnclosingMethod().getName());

        currencyUpdaterService.updateCurrenciesWithLiveExchangeRates();
    }
}
