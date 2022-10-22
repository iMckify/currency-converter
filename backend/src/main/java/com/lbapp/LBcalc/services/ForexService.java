package com.lbapp.LBcalc.services;

import com.lbapp.LBcalc.Application;
import com.lbapp.LBcalc.PriceHistoricalTransformer;
import com.lbapp.LBcalc.adapters.ForexAdapter;
import com.lbapp.LBcalc.models.PriceHistorical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ForexService {

    private final Application.PropsConfig propsConfig;

    private final ForexAdapter forexAdapter;

    public ForexService(Application.PropsConfig propsConfig, ForexAdapter forexAdapter) {
        this.propsConfig = propsConfig;
        this.forexAdapter = forexAdapter;
    }

    public List<PriceHistorical> getHistoricalFxRates(String symbol, String dateFrom, String dateTo) {
        URI historicalRatesAgainstEurUri = propsConfig.getHistory(symbol, dateFrom, dateTo);
        return forexAdapter.getFxRatesFrom(historicalRatesAgainstEurUri).stream()
                .map(PriceHistoricalTransformer::transformAgainstEurFrom)
                .filter(Objects::nonNull)
                .toList();
    }
}
