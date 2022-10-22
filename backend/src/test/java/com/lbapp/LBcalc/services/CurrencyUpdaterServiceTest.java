package com.lbapp.LBcalc.services;

import com.lbapp.LBcalc.Application.PropsConfig;
import com.lbapp.LBcalc.adapters.CurrencyUpdaterService;
import com.lbapp.LBcalc.adapters.ForexAdapter;
import com.lbapp.LBcalc.currency.CurrencyService;
import com.lbapp.LBcalc.models.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DataJpaTest
public class CurrencyUpdaterServiceTest {

    private final Map<String, BigDecimal> CURRENCIES = Map.of(
            "EURUSD", valueOf(0.971700),
            "EURAUD", valueOf(1.5254),
            "EURGBP", valueOf(1.126695)
    );

    @Autowired
    private PropsConfig propsConfig;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ForexAdapter forexAdapter;

    private CurrencyUpdaterService currencyUpdaterService;

    @BeforeEach
    void mockRepo() {
        currencyUpdaterService = new CurrencyUpdaterService(propsConfig, currencyService, forexAdapter);

        List<Currency> data = CURRENCIES
                .entrySet()
                .stream()
                .map(this::createCurrencyFrom)
                .toList();

        currencyService.saveCurrencies(data);
    }

    @Test
    public void shouldUpdateCurrenciesWithLiveExchangeRates() {
        // given
        List<Currency> initial = currencyService.getAllCurrencies();

        // when
        currencyUpdaterService.updateCurrenciesWithLiveExchangeRates();

        // then
        List<Currency> updated = currencyService.getAllCurrencies();
        assertNotEquals(initial, updated, "Currencies have not been updated");
        assertThatSomeUpdatedIdsDifferFromInitial(initial, updated);
    }

    private Currency createCurrencyFrom(Map.Entry<String, BigDecimal> entry) {
        return new Currency(null, entry.getKey(), entry.getValue());
    }

    private void assertThatSomeUpdatedIdsDifferFromInitial(List<Currency> initial, List<Currency> actualCurrencies) {
        List<String> initialSymbols = initial.stream().map(Currency::getSymbol).toList();
        Set<Integer> initialIds = initial.stream().map(Currency::getID).collect(Collectors.toUnmodifiableSet());
        Set<Integer> someNewIds = getCurrencyIdsFilteredBySymbol(initialSymbols, actualCurrencies);
        assertFalse(initialIds.containsAll(someNewIds));
    }

    private Set<Integer> getCurrencyIdsFilteredBySymbol(List<String> symbols, List<Currency> list) {
        return list.stream().filter(el -> symbols.contains(el.getSymbol())).map(Currency::getID).collect(Collectors.toUnmodifiableSet());
    }
}