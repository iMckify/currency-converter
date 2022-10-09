package com.lbapp.LBcalc.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lbapp.LBcalc.Application;
import com.lbapp.LBcalc.models.CcyAmt;
import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.models.FxRate;
import com.lbapp.LBcalc.models.HistoryFxRate;
import com.lbapp.LBcalc.repos.CurrentFxRatesRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ForexService {
    private final Application.PropsConfig propsConfig;

    private final CurrentFxRatesRepo currentFxRatesRepo;

    public ForexService(Application.PropsConfig propsConfig, CurrentFxRatesRepo currentFxRatesRepo) {
        this.propsConfig = propsConfig;
        this.currentFxRatesRepo = currentFxRatesRepo;
    }

    public List<CurrentFxRate> getAllCurrent() {
        return this.currentFxRatesRepo.findAll();
    }

    // todo lock
    public void updateFxRates() {
        URI uri = propsConfig.getForex().get(0);

        List<CurrentFxRate> freshRates = getFxRates(uri);

        if (freshRates.isEmpty()) {
            return;
        }

        List<CurrentFxRate> storedRates = this.currentFxRatesRepo.findAll();

        List<Integer> toDelete = new ArrayList<>();
        List<CurrentFxRate> toSave = new ArrayList<>();
        for (CurrentFxRate currentFxRate : freshRates) {
            Optional<CurrentFxRate> rateFilteredBySymbolOptional = storedRates
                    .stream()
                    .filter(saved -> saved.getSymbol().equals(currentFxRate.getSymbol()))
                    .findFirst();
            if (rateFilteredBySymbolOptional.isEmpty()) {
                toSave.add(currentFxRate);
            } else {
                CurrentFxRate rateStored = rateFilteredBySymbolOptional.get();
                if (!currentFxRate.getValue().setScale(8, RoundingMode.FLOOR).equals(rateStored.getValue())) {
                    toSave.add(currentFxRate);
                    toDelete.add(rateStored.getID());
                }
            }
        }

        if (!toSave.isEmpty()) {
            List<CurrentFxRate> deleteList = storedRates
                    .stream()
                    .filter(s -> toDelete.contains(s.getID()))
                    .toList();
            this.currentFxRatesRepo.deleteAll(deleteList);

            toSave = toSave
                    .stream()
                    .map(currentFxRate -> new CurrentFxRate(
                            currentFxRate.getID(),
                            currentFxRate.getSymbol(),
                            currentFxRate.getValue().setScale(8, RoundingMode.FLOOR)
                    ))
                    .toList();
            this.currentFxRatesRepo.saveAllAndFlush(toSave);
        }
    }

    // todo currently can return single empty HistoricalRate
    public List<HistoryFxRate> getRatesHistory(String symbol, String dateFrom, String dateTo) {
        URI uri = propsConfig.getForex().get(1);
        try {
            uri = formatUrl(uri, symbol, dateFrom, dateTo);
        } catch (URISyntaxException e) {
            return List.of();
        }

        List<FxRate> fxRates = getFxRatesFrom(uri);

        List<HistoryFxRate> mapped = fxRates.stream().map(fxRate -> {
                    HistoryFxRate historicalRate = new HistoryFxRate();

                    List<CcyAmt> currenciesAndAmounts = fxRate.getRates();
                    if (currenciesAndAmounts.size() != 2) {
                        return historicalRate;
                    }

                    historicalRate.setDate(fxRate.getDate());
                    historicalRate.setValue(currenciesAndAmounts.get(currenciesAndAmounts.size() - 1).getAmount());

                    return historicalRate;
                })
                .filter(ratesHistory ->
                        (ratesHistory.getDate() != null && !ratesHistory.getDate().isBlank()) &&
                                (ratesHistory.getValue() != null && ratesHistory.getValue().compareTo(BigDecimal.ZERO) > 0)
                ).toList();

        return mapped;
    }

    private URI formatUrl(URI uri, String symbol, String dateFrom, String dateTo) throws URISyntaxException {
        String urlStr = uri.toString();
        urlStr = MessageFormat.format(urlStr, symbol, dateFrom, dateTo);
        return new URI(urlStr);
    }

    // todo currently can return single empty CurrentFxRate
    private List<CurrentFxRate> getFxRates(URI uri) {
        List<FxRate> rates = getFxRatesFrom(uri);

        List<CurrentFxRate> mapped = rates.stream().map(fx -> {
                    CurrentFxRate cfx = new CurrentFxRate();

                    List<CcyAmt> currList = fx.getRates();
                    if (currList.size() != 2) {
                        return cfx;
                    }

                    String symbol = currList.stream().map(CcyAmt::getCurrency).collect(Collectors.joining());
                    cfx.setSymbol(symbol);

                    cfx.setValue(currList.get(currList.size() - 1).getAmount());

                    return cfx;
                })
                .filter(cfx ->
                        (cfx.getSymbol() != null && cfx.getSymbol().length() > 0) &&
                                (cfx.getValue() != null && cfx.getValue().compareTo(BigDecimal.ZERO) > 0)
                ).collect(Collectors.toList());

        if (rates.size() == 0 || mapped.size() == 0) {
            return List.of();
        }

        return mapped;
    }

    private List<FxRate> getFxRatesFrom(URI uri) {
        InputStream inputStream = getFrom(uri);
        return parseFxRatesFrom(inputStream);
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

    private List<FxRate> parseFxRatesFrom(InputStream inputStream) {
        requireNonNull(inputStream, "InputStream of fx rates can not be null");
        try {
            return new XmlMapper().readValue(inputStream, new TypeReference<List<FxRate>>() {
            });
        } catch (Exception ignored) {
        }
        return List.of();
    }
}
