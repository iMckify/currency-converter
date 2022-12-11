package com.lbapp.LBcalc.forex;

import com.lbapp.LBcalc.currency.models.CurrencyDto;
import com.lbapp.LBcalc.forex.models.FxRateDto;
import com.lbapp.LBcalc.forex.models.PriceHistorical;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.*;
import static java.time.LocalDate.now;
import static java.util.Map.entry;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(PriceHistoricalTransformer.class)
class PriceHistoricalTransformerTest {

    @Autowired
    private PriceHistoricalTransformer transformer;

    @Test
    public void shouldTransformAgainstEurFrom() {
        // given
        BigDecimal amountInForeignCurrency = valueOf(0.971700);
        FxRateDto rateDto = new FxRateDto();
        CurrencyDto usd = createCurrencyDtoFrom(entry("USD", amountInForeignCurrency));
        CurrencyDto eur = createCurrencyDtoFrom(entry("EUR", ONE));
        rateDto.setCurrencyEntries(List.of(eur, usd));

        String now = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now());
        rateDto.setDate(now);

        // when
        PriceHistorical priceHistorical = transformer.transformAgainstEurFrom(rateDto);

        // then
        assertNotNull(priceHistorical);
        assertEquals(now, priceHistorical.getDate());
        assertEquals(amountInForeignCurrency, priceHistorical.getRate());
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
    public void shouldFailToTransformAgainstEurFromWhenDateBlank() {
        // given
        FxRateDto rateDto = new FxRateDto();
        CurrencyDto usd = createCurrencyDtoFrom(entry("USD", valueOf(0.971700)));
        CurrencyDto eur = createCurrencyDtoFrom(entry("EUR", ONE));
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

        String now = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now());
        rateDto.setDate(now);

        // when
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> transformer.transformAgainstEurFrom(rateDto)
        );
        assertEquals(exception.getMessage(), "Failed to transform fx rate");
    }

    private CurrencyDto createCurrencyDtoFrom(Map.Entry<String, BigDecimal> entry) {
        CurrencyDto dto = new CurrencyDto();
        dto.setIsoCode(entry.getKey());
        dto.setAmount(entry.getValue());
        return dto;
    }
}