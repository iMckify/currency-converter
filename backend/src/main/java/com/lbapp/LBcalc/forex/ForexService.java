package com.lbapp.LBcalc.forex;

import com.lbapp.LBcalc.Application.PropsConfig;
import com.lbapp.LBcalc.forex.models.PriceHistorical;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class ForexService {

    private final PropsConfig propsConfig;

    private final ForexAdapter forexAdapter;

    private final PriceHistoricalTransformer transformer;

    public ForexService(PropsConfig propsConfig, ForexAdapter forexAdapter, PriceHistoricalTransformer transformer) {
        this.propsConfig = propsConfig;
        this.forexAdapter = forexAdapter;
        this.transformer = transformer;
    }

    public List<PriceHistorical> getHistoricalFxRates(String symbol, String dateFrom, String dateTo) {
        URI historicalRatesAgainstEurUri = propsConfig.getHistory(symbol, dateFrom, dateTo);

        return forexAdapter.getFxRatesFrom(historicalRatesAgainstEurUri).stream()
                .map(transformer::transformAgainstEurFrom)
                .toList();
    }
}
