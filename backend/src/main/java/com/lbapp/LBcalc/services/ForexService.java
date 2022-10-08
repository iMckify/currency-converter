package com.lbapp.LBcalc.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lbapp.LBcalc.LBcalc;
import com.lbapp.LBcalc.models.CcyAmt;
import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.models.FxRate;
import com.lbapp.LBcalc.models.HistoryFxRate;
import com.lbapp.LBcalc.repos.CurrentFxRatesRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ForexService {
    public static final Logger logger = LoggerFactory.getLogger(ForexService.class);

    private final LBcalc.PropsConfig propsConfig;

    private final CurrentFxRatesRepo currentFxRatesRepo;

    public ForexService(LBcalc.PropsConfig propsConfig, CurrentFxRatesRepo currentFxRatesRepo) {
        this.propsConfig = propsConfig;
        this.currentFxRatesRepo = currentFxRatesRepo;
    }

    public List<CurrentFxRate> getAllCurrent() {
        return this.currentFxRatesRepo.findAll();
    }

    public void updateFxRates() {
        URL url = propsConfig.getForex().get(0);

        List<CurrentFxRate> allRates = getFxRates(url);

        if (allRates.size() == 0) {
            return;
        }

        List<CurrentFxRate> stored = this.currentFxRatesRepo.findAll();

        List<Integer> toDelete = new ArrayList<>();
        List<CurrentFxRate> toSave = new ArrayList<>();
        for (CurrentFxRate cfx : allRates) {
            Optional<CurrentFxRate> opt = stored.stream().filter(saved -> saved.getSymbol().equals(cfx.getSymbol())).findFirst();
            if (!opt.isPresent()) {
                toSave.add(cfx);
            } else {
                CurrentFxRate rateStored = opt.get();
                if (!cfx.getValue().setScale(8, BigDecimal.ROUND_FLOOR).equals(rateStored.getValue())) {
                    toSave.add(cfx);
                    toDelete.add(rateStored.getID());
                }
            }
        }

        if (toSave.size() > 0) {
            List<CurrentFxRate> deleteList = stored.stream().filter(s -> toDelete.contains(s.getID())).collect(Collectors.toList());
            this.currentFxRatesRepo.deleteAll(deleteList);

            toSave = toSave.stream().peek(x -> x.setValue(x.getValue().setScale(8, BigDecimal.ROUND_FLOOR))).collect(Collectors.toList());
            this.currentFxRatesRepo.saveAll(toSave);
        }
    }

    public List<HistoryFxRate> getRatesHistory(String symbol, String dateFrom, String dateTo) {
        URL url = propsConfig.getForex().get(1);
        try {
            url = formatUrl(url, symbol, dateFrom, dateTo);
        } catch (MalformedURLException e) {
            return new ArrayList<>();
        }

//        return getFxRates(url);

        InputStream inputStream = get(url);
        List<FxRate> rates = parse(inputStream);

        List<HistoryFxRate> mapped = rates.stream().map(fx -> {
            HistoryFxRate hfx = new HistoryFxRate();

            List<CcyAmt> currList = fx.getRates();
            if (currList.size() != 2) {
                return hfx;
            }

            hfx.setDate(fx.getDate());
            hfx.setValue(currList.get(currList.size() - 1).getAmount());

            return hfx;
        })
                .filter(hfx ->
                (hfx.getDate() != null && hfx.getDate().length() > 0) &&
                        (hfx.getValue() != null && hfx.getValue().compareTo(BigDecimal.ZERO) > 0)
        ).collect(Collectors.toList());

        return mapped;
    }

    private List<CurrentFxRate> getFxRates(URL url) {
        InputStream inputStream = get(url);
        List<FxRate> rates = parse(inputStream);

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
            return new ArrayList<>();
        }

        return mapped;
    }

    private URL formatUrl(URL url, String symbol, String dateFrom, String dateTo) throws MalformedURLException {
        String urlStr = url.toString();
        urlStr = MessageFormat.format(urlStr, symbol, dateFrom, dateTo);
        return new URL(urlStr);
    }

    private InputStream get(URL url) {
        InputStream inputStream = null;
        try {
//            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8); // use to get LB errors
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .setHeader("Content-type", "application/xml")
                    .GET()
                    .build();

//            int code = response.getStatusLine().getStatusCode();
            HttpResponse<InputStream> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofInputStream());
            inputStream = response.body();
        } catch (Exception e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return inputStream;
    }

    private List<FxRate> parse(InputStream inputStream) {
        try {
            XmlMapper xmlMapper = new XmlMapper();

            return xmlMapper.readValue(inputStream, new TypeReference<List<FxRate>>() {});
        } catch (Exception e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return new ArrayList<FxRate>();
    }
}
