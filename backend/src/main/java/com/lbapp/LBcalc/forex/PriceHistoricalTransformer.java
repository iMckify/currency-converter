package com.lbapp.LBcalc.forex;

import com.lbapp.LBcalc.currency.models.CurrencyDto;
import com.lbapp.LBcalc.forex.models.FxRateDto;
import com.lbapp.LBcalc.forex.models.PriceHistorical;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

@Component
public class PriceHistoricalTransformer {

    public PriceHistorical transformAgainstEurFrom(FxRateDto dto) {
        PriceHistorical historicalRate = new PriceHistorical();

        List<CurrencyDto> currencyEntries = dto.getCurrencyEntries();
        if (currencyEntries.size() != 2) {
            throw new IllegalStateException(format("Invalid size of currencies: %s", currencyEntries.size()));
        }

        historicalRate.setDate(dto.getDate());
        CurrencyDto lastCurrency = currencyEntries.get(currencyEntries.size() - 1);
        historicalRate.setRate(lastCurrency.getAmount());

        return getHistoricalRateOrFail(historicalRate);
    }

    private PriceHistorical getHistoricalRateOrFail(PriceHistorical historicalRate) {
        return of(historicalRate)
                .filter(hasDate())
                .filter(hasPositiveRate())
                .orElseThrow(() -> new IllegalStateException("Failed to transform fx rate"));
    }

    private Predicate<PriceHistorical> hasDate() {
        return priceHistorical -> ofNullable(priceHistorical.getDate())
                .filter(not(String::isBlank))
                .isPresent();
    }

    private Predicate<PriceHistorical> hasPositiveRate() {
        return priceHistorical -> ofNullable(priceHistorical.getRate())
                .filter(value -> value.compareTo(ZERO) > 0)
                .isPresent();
    }
}
