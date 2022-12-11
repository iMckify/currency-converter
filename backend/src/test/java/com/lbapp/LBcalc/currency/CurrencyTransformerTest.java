package com.lbapp.LBcalc.currency;

import com.lbapp.LBcalc.currency.models.Currency;
import com.lbapp.LBcalc.currency.models.CurrencyDto;
import com.lbapp.LBcalc.forex.models.FxRateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.lbapp.LBcalc.currency.CurrencyTransformer.SCALE;
import static java.math.BigDecimal.*;
import static java.util.Map.entry;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(CurrencyTransformer.class)
class CurrencyTransformerTest {

    @Autowired
    private CurrencyTransformer transformer;

    @Test
    public void shouldTransformScaleWhenTooLittle() {
        // given
        Currency currency = createCurrencyFrom(entry("EURUSD", valueOf(0.971700)));

        // when
        Currency transformed = transformer.transformScale(currency);

        // then
        BigDecimal newValue = transformed.getValue();
        String fraction = newValue.toPlainString().split("\\.")[1];

        assertThat(transformed.getSymbol(), equalTo(currency.getSymbol()));
        assertThat(newValue.scale(), comparesEqualTo(SCALE));
        assertThat(fraction, equalTo("97170000"));
    }

    @Test
    public void shouldTransformScaleWhenTooMany() {
        // given
        Currency currency = createCurrencyFrom(entry("EURUSD", valueOf(0.971745454545)));

        // when
        Currency transformed = transformer.transformScale(currency);

        // then
        BigDecimal newValue = transformed.getValue();
        String fraction = newValue.toPlainString().split("\\.")[1];

        assertEquals(currency.getSymbol(), transformed.getSymbol());
        assertEquals(SCALE, newValue.scale());
        assertEquals("97174545", fraction);
    }

    @Test
    public void shouldTransformAgainstEurFrom() {
        // given
        BigDecimal amountInForeignCurrency = valueOf(0.971700);
        FxRateDto rateDto = new FxRateDto();
        CurrencyDto usd = createCurrencyDtoFrom(entry("USD", amountInForeignCurrency));
        CurrencyDto eur = createCurrencyDtoFrom(entry("EUR", ONE));
        rateDto.setCurrencyEntries(List.of(eur, usd));

        // when
        Currency currency = transformer.transformAgainstEurFrom(rateDto);

        // then
        assertNotNull(currency);
        assertEquals("EURUSD", currency.getSymbol());
        assertEquals(amountInForeignCurrency, currency.getValue());
    }

    @Test
    public void shouldFailToTransformAgainstEurFromWhenInvalidSizeOfCurrencies() {
        // given
        FxRateDto rateDto = new FxRateDto();
        CurrencyDto usd = createCurrencyDtoFrom(entry("USD", valueOf(0.971700)));
        CurrencyDto eur = createCurrencyDtoFrom(entry("EUR", ONE));
        CurrencyDto aud = createCurrencyDtoFrom(entry("AUD", valueOf(1.5493)));
        rateDto.setCurrencyEntries(List.of(eur, usd, aud));

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> transformer.transformAgainstEurFrom(rateDto)
        );
        assertThat(exception.getMessage(), containsString("Invalid size of currencies"));
    }

    @Test
    public void shouldFailToTransformAgainstEurFromWhenSymbolBlank() {
        // given
        FxRateDto rateDto = new FxRateDto();
        CurrencyDto usd = createCurrencyDtoFrom(entry("", valueOf(0.971700)));
        CurrencyDto eur = createCurrencyDtoFrom(entry("", ONE));
        rateDto.setCurrencyEntries(List.of(eur, usd));

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> transformer.transformAgainstEurFrom(rateDto)
        );
        assertEquals(exception.getMessage(), "Failed to transform fx rate");
    }

    @Test
    public void shouldFailToTransformAgainstEurFromWhenValueNonPositive() {
        // given
        FxRateDto rateDto = new FxRateDto();
        CurrencyDto usd = createCurrencyDtoFrom(entry("USD", ZERO));
        CurrencyDto eur = createCurrencyDtoFrom(entry("EUR", ZERO));
        rateDto.setCurrencyEntries(List.of(eur, usd));

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> transformer.transformAgainstEurFrom(rateDto)
        );
        assertEquals(exception.getMessage(), "Failed to transform fx rate");
    }

    private Currency createCurrencyFrom(Map.Entry<String, BigDecimal> entry) {
        return new Currency(null, entry.getKey(), entry.getValue());
    }

    private CurrencyDto createCurrencyDtoFrom(Map.Entry<String, BigDecimal> entry) {
        CurrencyDto dto = new CurrencyDto();
        dto.setIsoCode(entry.getKey());
        dto.setAmount(entry.getValue());
        return dto;
    }
}