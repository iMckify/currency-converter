package com.lbapp.LBcalc.adapters;

import com.lbapp.LBcalc.Application;
import com.lbapp.LBcalc.CurrencyTransformer;
import com.lbapp.LBcalc.currency.CurrencyService;
import com.lbapp.LBcalc.models.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.math.RoundingMode.FLOOR;

@Slf4j
@Service
public class CurrencyUpdaterService {

    private final Application.PropsConfig propsConfig;

    private final CurrencyService currencyService;

    private final ForexAdapter forexAdapter;

    public CurrencyUpdaterService(Application.PropsConfig propsConfig, CurrencyService currencyService, ForexAdapter forexAdapter) {
        this.propsConfig = propsConfig;
        this.currencyService = currencyService;
        this.forexAdapter = forexAdapter;
    }

    public void updateCurrenciesWithLiveExchangeRates() {
        URI liveRatesUri = propsConfig.getCurrent();

        List<Currency> liveRates = getFxRates(liveRatesUri);

        if (liveRates.isEmpty()) {
            log.warn("No currencies have been updated because fetched no live rates");
            return;
        }

        List<Currency> previousCurrencies = currencyService.getAllCurrencies();

        List<Integer> toDelete = new ArrayList<>();
        List<Currency> toSave = new ArrayList<>();

        for (Currency liveCurrency : liveRates) {
            Optional<Currency> previousRateOptional = previousCurrencies.stream()
                    .filter(saved -> saved.getSymbol().equals(liveCurrency.getSymbol()))
                    .findFirst();
            if (previousRateOptional.isEmpty()) {
                toSave.add(liveCurrency);
            } else {
                BigDecimal liveValue = liveCurrency.getValue().setScale(8, FLOOR);
                Currency prevCurrency = previousRateOptional.get();
                // BigDecimal.equals compares scale too
                if (!liveValue.equals(prevCurrency.getValue())) {
                    toSave.add(liveCurrency);
                    toDelete.add(prevCurrency.getID());
                }
            }
        }

        if (!toSave.isEmpty()) {
            currencyService.deleteCurrencies(toDelete);
            toSave = toSave.stream().map(CurrencyTransformer::transformScale).toList();
            currencyService.saveCurrencies(toSave);
        }
    }

    private List<Currency> getFxRates(URI uri) {
        return forexAdapter.getFxRatesFrom(uri).stream()
                .map(CurrencyTransformer::transformAgainstEurFrom)
                .filter(Objects::nonNull)
                .toList();
    }
}
