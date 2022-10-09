package com.lbapp.LBcalc.controllers;

import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.models.HistoryFxRate;
import com.lbapp.LBcalc.services.ConverterService;
import com.lbapp.LBcalc.services.ForexService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/Forex")
public class ForexController {
    private final ForexService forexService;

    private final ConverterService converterService;

    public ForexController(ForexService forexService, ConverterService converterService) {
        this.forexService = forexService;
        this.converterService = converterService;
    }

    @GetMapping("/current")
    public List<CurrentFxRate> getLastPrices(){
        return this.forexService.getAllCurrent();
    }

    @GetMapping("/history/{symbol}/{dateFrom}/{dateTo}")
    public List<HistoryFxRate> getPriceHistory(
            @PathVariable(value = "symbol") String symbol,
            @PathVariable(value = "dateFrom") String dateFrom,
            @PathVariable(value = "dateTo") String dateTo
    ) {
        return this.forexService.getRatesHistory(symbol, dateFrom, dateTo);
    }

    @GetMapping("/convert/{symbolFrom}/{symbolTo}/{amount}")
    public BigDecimal convert(
            @PathVariable(value = "symbolFrom") String symbolFrom,
            @PathVariable(value = "symbolTo") String symbolTo,
            @PathVariable(value = "amount") BigDecimal amount
    ){
        return this.converterService.convertAPI(symbolFrom, symbolTo, amount);
    }
}
