package com.lbapp.LBcalc.currency;

import com.lbapp.LBcalc.currency.models.Currency;
import com.lbapp.LBcalc.currency.models.CurrencyDto;
import com.lbapp.LBcalc.forex.models.FxRateDto;

import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.FLOOR;
import static java.util.Objects.nonNull;

public class CurrencyTransformer {

    public static Currency transformScale(Currency previous) {
        Currency currency = previous.clone();
        currency.setValue(previous.getValue().setScale(8, FLOOR));
        return currency;
    }

    public static Currency transformAgainstEurFrom(FxRateDto dto) {
        Currency currency = new Currency();

        List<CurrencyDto> currencyPairs = dto.getCurrencyPairs();
        if (currencyPairs.size() != 2) {
            return null;
        }

        String symbol = currencyPairs.stream().map(CurrencyDto::getIsoCode).collect(Collectors.joining());
        currency.setSymbol(symbol);
        currency.setValue(currencyPairs.get(currencyPairs.size() - 1).getAmount());

        if (!hasSymbol(currency) || !hasValidRate(currency)) {
            return null;
        }

        return currency;
    }

    private static boolean hasSymbol(Currency currency) {
        return nonNull(currency.getSymbol()) && !currency.getSymbol().isBlank();
    }

    private static boolean hasValidRate(Currency currency) {
        return nonNull(currency.getValue()) && currency.getValue().compareTo(ZERO) > 0;
    }
}
