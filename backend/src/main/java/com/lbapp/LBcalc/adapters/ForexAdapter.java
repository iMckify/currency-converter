package com.lbapp.LBcalc.adapters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lbapp.LBcalc.LBcalc;
import com.lbapp.LBcalc.models.CcyAmt;
import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.models.FxRate;
import com.lbapp.LBcalc.repos.CurrentFxRatesRepo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ForexAdapter {
    public static final Logger logger = LoggerFactory.getLogger(ForexAdapter.class);

    @Autowired
    private LBcalc.PropsConfig propsConfig;

    @Autowired
    private CurrentFxRatesRepo currentFxRatesRepo;

    //@Scheduled(cron = "@weekly")
    // unknown LB api update frequency
    @PostConstruct
    private void updateFxRates() {
        logger.info("Executing scheduled task {}()", new Object(){}.getClass().getEnclosingMethod().getName());

        URL url = propsConfig.getForex().get(0);

        InputStream inputStream = get(String.valueOf(url));
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
            return;
        }

        List<CurrentFxRate> stored = this.currentFxRatesRepo.findAll();

        List<Integer> toDelete = new ArrayList<>();
        List<CurrentFxRate> toSave = new ArrayList<>();
        for (CurrentFxRate cfx : mapped) {
            Optional<CurrentFxRate> opt = stored.stream().filter(saved -> saved.getSymbol().equals(cfx.getSymbol())).findFirst();
            if (!opt.isPresent()) {
                toSave.add(cfx);
            } else {
                CurrentFxRate rateStored = opt.get();
                if (!cfx.getValue().equals(rateStored.getValue())) {
                    toSave.add(cfx);
                    toDelete.add(rateStored.getID());
                }
            }
        }

        if (toSave.size() > 0) {
            List<CurrentFxRate> deleteList = stored.stream().filter(s -> toDelete.contains(s.getID())).collect(Collectors.toList());
            this.currentFxRatesRepo.deleteAll(deleteList);

            this.currentFxRatesRepo.saveAll(toSave);
        }

        System.out.println();
    }

    private InputStream get(String url) {
        InputStream inputStream = null;
        try {
            CloseableHttpClient client = HttpClients.createMinimal();
            HttpUriRequest request = new HttpGet(url);
            request.setHeader("Content-type", "application/xml");
            CloseableHttpResponse response = client.execute(request);
            inputStream = response.getEntity().getContent();
//            String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8); // use to get LB errors

//            int code = response.getStatusLine().getStatusCode();
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
