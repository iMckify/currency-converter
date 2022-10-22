package com.lbapp.LBcalc.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lbapp.LBcalc.Application.PropsConfig;
import com.lbapp.LBcalc.CurrencyTransformer;
import com.lbapp.LBcalc.PriceHistoricalTransformer;
import com.lbapp.LBcalc.models.Currency;
import com.lbapp.LBcalc.models.FxRateDto;
import com.lbapp.LBcalc.models.PriceHistorical;
import com.lbapp.LBcalc.repos.CurrencyRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.math.RoundingMode.FLOOR;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ForexService {
    private final PropsConfig propsConfig;

    private final CurrencyRepo currencyRepo;

    public ForexService(PropsConfig propsConfig, CurrencyRepo currencyRepo) {
        this.propsConfig = propsConfig;
        this.currencyRepo = currencyRepo;
    }

    public List<Currency> getAllCurrencies() {
        return this.currencyRepo.findAll();
    }

    public void updateCurrenciesWithLiveExchangeRates() {
        URI liveRatesUri = propsConfig.getCurrent();

        List<Currency> liveRates = getFxRates(liveRatesUri);

        if (liveRates.isEmpty()) {
            log.warn("No currencies have been updated because fetched no live rates");
            return;
        }

        List<Currency> previousCurrencies = getAllCurrencies();

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
            this.currencyRepo.deleteAllById(toDelete);
            toSave = toSave.stream().map(CurrencyTransformer::transformScale).toList();
            this.currencyRepo.saveAllAndFlush(toSave);
        }
    }

    public List<PriceHistorical> getHistoricalFxRates(String symbol, String dateFrom, String dateTo) {
        URI historicalRatesAgainstEurUri = propsConfig.getHistory(symbol, dateFrom, dateTo);
        return getFxRatesFrom(historicalRatesAgainstEurUri).stream()
                .map(PriceHistoricalTransformer::transformAgainstEurFrom)
                .filter(Objects::nonNull)
                .toList();
    }

    private URI formatUrl(URI uri, String symbol, String dateFrom, String dateTo) throws URISyntaxException {
        String urlStr = uri.toString();
        urlStr = MessageFormat.format(urlStr, symbol, dateFrom, dateTo);
        return new URI(urlStr);
    }

    private List<FxRateDto> getFxRatesFrom(URI uri) {
        InputStream inputStream = getFrom(uri);
        return parseFxRatesFrom(inputStream);
    }

    private List<Currency> getFxRates(URI uri) {
        return getFxRatesFrom(uri).stream()
                .map(CurrencyTransformer::transformAgainstEurFrom)
                .filter(Objects::nonNull)
                .toList();
    }

    private InputStream getFrom(URI uri) {
        requireNonNull(uri, "Uri can not be null");
        InputStream inputStream = null;
        try {
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .setHeader("Content-type", "application/xml")
                    .GET()
                    .build();

            HttpResponse<InputStream> response = HttpClient
                    .newBuilder()
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());
            inputStream = response.body();
        } catch (Exception ignored) {
        }
        return inputStream;
    }

    private List<FxRateDto> parseFxRatesFrom(InputStream inputStream) {
        requireNonNull(inputStream, "InputStream of fx rates can not be null");
        try {
            return new XmlMapper().readValue(inputStream, new TypeReference<List<FxRateDto>>() {
            });
        } catch (Exception ignored) {
            return List.of();
        }
    }
}
