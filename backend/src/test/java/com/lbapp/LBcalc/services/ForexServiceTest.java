package com.lbapp.LBcalc.services;

import com.lbapp.LBcalc.Application.PropsConfig;
import com.lbapp.LBcalc.models.Currency;
import com.lbapp.LBcalc.repos.CurrencyRepo;
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
public class ForexServiceTest {

    private final Map<String, BigDecimal> CURRENCIES = Map.of(
            "EURUSD", valueOf(0.971700),
            "EURAUD", valueOf(1.5254),
            "EURGBP", valueOf(1.126695)
    );

    @Autowired
    private CurrencyRepo repo;

    @Autowired
    private PropsConfig propsConfig;

    private ForexService service;

    @BeforeEach
    void mockRepo() {
        service = new ForexService(propsConfig, repo);

        List<Currency> data = CURRENCIES
                .entrySet()
                .stream()
                .map(this::createCurrencyFrom)
                .toList();

        repo.saveAll(data);
    }

    @Test
    public void shouldUpdateCurrenciesWithLiveExchangeRates() {
        // given
        List<Currency> initial = service.getAllCurrencies();

        // when
        service.updateCurrenciesWithLiveExchangeRates();

        // then
        List<Currency> updated = service.getAllCurrencies();
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
