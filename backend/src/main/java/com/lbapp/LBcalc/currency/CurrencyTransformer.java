package com.lbapp.LBcalc.currency;

import com.lbapp.LBcalc.currency.models.Currency;
import com.lbapp.LBcalc.currency.models.CurrencyDto;
import com.lbapp.LBcalc.forex.models.FxRateDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

@Component
public class CurrencyTransformer {

    public static final int SCALE = 8;

    public Currency transformScale(Currency previous) {
        Currency currency = previous.clone();
        currency.setValue(previous.getValue().setScale(SCALE, FLOOR));
        return currency;
    }

    public Currency transformAgainstEurFrom(FxRateDto dto) {
        Currency currency = new Currency();

        List<CurrencyDto> currencyEntries = dto.getCurrencyEntries();
        if (currencyEntries.size() != 2) {
            throw new IllegalStateException(format("Invalid size of currencies: %s", currencyEntries.size()));
        }

        String symbol = currencyEntries.stream().map(CurrencyDto::getIsoCode).collect(joining());
        currency.setSymbol(symbol);
        CurrencyDto lastCurrency = currencyEntries.get(currencyEntries.size() - 1);
        currency.setValue(lastCurrency.getAmount());

        return getCurrencyOrFail(currency);
    }

    private Currency getCurrencyOrFail(Currency currency) {
        return of(currency)
                .filter(hasSymbol())
                .filter(hasPositiveValue())
                .orElseThrow(() -> new IllegalStateException("Failed to transform fx rate"));
    }

    private Predicate<Currency> hasSymbol() {
        return currency -> ofNullable(currency.getSymbol())
                .filter(not(String::isBlank))
                .isPresent();
    }

    private Predicate<Currency> hasPositiveValue() {
        return currency -> ofNullable(currency.getValue())
                .filter(value -> value.compareTo(ZERO) > 0)
                .isPresent();
    }
}
