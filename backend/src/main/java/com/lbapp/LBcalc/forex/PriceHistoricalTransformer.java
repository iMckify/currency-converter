package com.lbapp.LBcalc.forex;

import com.lbapp.LBcalc.currency.models.CurrencyDto;
import com.lbapp.LBcalc.forex.models.FxRateDto;
import com.lbapp.LBcalc.forex.models.PriceHistorical;

import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.nonNull;

public class PriceHistoricalTransformer {

    public static PriceHistorical transformAgainstEurFrom(FxRateDto dto) {
        PriceHistorical historicalRate = new PriceHistorical();

        List<CurrencyDto> currencyPairs = dto.getCurrencyPairs();
        if (currencyPairs.size() != 2) {
            return null;
        }

        historicalRate.setDate(dto.getDate());
        historicalRate.setValue(currencyPairs.get(currencyPairs.size() - 1).getAmount());

        if (!hasDate(historicalRate) || !hasValidRate(historicalRate)) {
            return null;
        }

        return historicalRate;
    }

    private static boolean hasDate(PriceHistorical priceHistorical) {
        return nonNull(priceHistorical.getDate()) && !priceHistorical.getDate().isBlank();
    }

    private static boolean hasValidRate(PriceHistorical priceHistorical) {
        return nonNull(priceHistorical.getValue()) && priceHistorical.getValue().compareTo(ZERO) > 0;
    }
}
