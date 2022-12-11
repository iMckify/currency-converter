package com.lbapp.LBcalc.forex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lbapp.LBcalc.forex.models.FxRateDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static java.io.InputStream.nullInputStream;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(ForexAdapter.class)
public class ForexAdapterTest {

    @Autowired
    private ForexAdapter forexAdapter;

    private static List<FxRateDto> fxRates;

    @AfterAll
    public static void printFxRates() {
        if (fxRates != null && !fxRates.isEmpty()) {
            fxRates.forEach(System.out::println);
            System.out.println();
            fxRates.clear();
        }
    }

    @Test
    public void shouldGetAndParseFxRates() throws MalformedURLException, URISyntaxException {
        // given
        String url = "https://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrentFxRates?tp=eu";

        // when
        fxRates = getFxRatesFrom(url);

        // then
        assertTrue(fxRates.size() > 0);
        assertTrue(fxRates.stream().allMatch(fxRateDto -> fxRateDto.getCurrencyEntries().size() == 2));
    }

    @Test
    public void shouldReturnEmptyListWhenBadUrl() throws MalformedURLException, URISyntaxException {
        // given
        String url = "https://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrentFxRates?tp=randomstring";

        // when
        fxRates = getFxRatesFrom(url);

        // then
        assertTrue(fxRates.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListWhenResponseCannotBeParsedToFxRates() throws MalformedURLException, URISyntaxException {
        // given
        String url = "https://www.google.com";

        // when
        fxRates = getFxRatesFrom(url);

        // then
        assertTrue(fxRates.isEmpty());
    }

    @Test
    public void shouldFailParsingWhenBlankUrl() {
        // given
        String url = "";

        // when
        assertThrows(MalformedURLException.class, () -> forexAdapter.getFrom(new URL(url).toURI()));
    }

    @Test
    public void shouldFailParsingWhenNullInputStream() {
        // given
        InputStream inputStream = null;

        // when
        NullPointerException exception = assertThrows(NullPointerException.class, () -> forexAdapter.parseFxRatesFrom(inputStream));

        // then
        assertEquals("InputStream of fx rates can not be null", exception.getMessage());
    }

    @Test
    public void shouldReturnEmptyListWhenEmptyInputStream() {
        // given
        InputStream inputStream = nullInputStream();

        // when
        fxRates = forexAdapter.parseFxRatesFrom(inputStream);

        // then
        assertTrue(fxRates.isEmpty());
    }

    @Test
    public void shouldNotFailWhenDeserializing3Pairs() throws JsonProcessingException {
        String json = """
                    <FxRates xmlns="http://www.lb.lt/WebServices/FxRates">
                        <FxRate>
                            <Tp>EU</Tp>
                            <Dt>2022-10-14</Dt>
                            <CcyAmt>
                              <Ccy>EUR</Ccy>
                              <Amt>1</Amt>
                            </CcyAmt>
                            <CcyAmt>
                              <Ccy>AUD</Ccy>
                              <Amt>1.5493</Amt>
                            </CcyAmt>
                            <CcyAmt>
                              <Ccy>CAD</Ccy>
                              <Amt>1.1111</Amt>
                            </CcyAmt>
                          </FxRate>
                    </FxRates>
                """;
        List<FxRateDto> rates = new XmlMapper().readValue(json, new TypeReference<List<FxRateDto>>() {
        });
        assertEquals(3, rates.get(0).getCurrencyEntries().size());
    }

    private List<FxRateDto> getFxRatesFrom(String url) throws MalformedURLException, URISyntaxException {
        return forexAdapter.getFxRatesFrom(new URL(url).toURI());
    }
}
