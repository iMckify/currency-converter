package com.lbapp.LBcalc.controllers;

import com.lbapp.LBcalc.services.ForexService;
import com.lbapp.LBcalc.currency.CurrencyService;
import com.lbapp.LBcalc.models.Currency;
import com.lbapp.LBcalc.models.PriceHistorical;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/Forex")
public class ForexController {

    private final ForexService forexService;

    private final CurrencyService currencyService;

    public ForexController(ForexService forexService, CurrencyService currencyService) {
        this.forexService = forexService;
        this.currencyService = currencyService;
    }

    @GetMapping("/current")
    public List<Currency> getLastPrices() {
        return currencyService.getAllCurrencies();
    }

    @GetMapping("/history/{symbol}/{dateFrom}/{dateTo}")
    public List<PriceHistorical> getPriceHistory(
            @PathVariable(value = "symbol") String symbol,
            @PathVariable(value = "dateFrom") String dateFrom,
            @PathVariable(value = "dateTo") String dateTo
    ) {
        return forexService.getHistoricalFxRates(symbol, dateFrom, dateTo);
    }
}
