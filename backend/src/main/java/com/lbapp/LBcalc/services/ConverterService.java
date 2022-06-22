package com.lbapp.LBcalc.services;

import com.lbapp.LBcalc.models.CurrentFxRate;
import com.lbapp.LBcalc.repos.CurrentFxRatesRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ConverterService {
    public static final Logger logger = LoggerFactory.getLogger(ConverterService.class);

    @Autowired
    private CurrentFxRatesRepo currentFxRatesRepo;

    // https://sdw.ecb.europa.eu/curConverter.do
    // 100 USD to AUD
    // 100 / 1,0517 USD * 1,5061 AUD = 143.20623752
    // input / first * second
    public double convert(String symbolSelected, String symbolTarget, double input) {
        List<CurrentFxRate> allRates = this.currentFxRatesRepo.findAll();

        BigDecimal firstRate = null;
        if (symbolSelected.equals("EUR")) {
            firstRate = new BigDecimal(1);
        } else {
            firstRate = this.currentFxRatesRepo.findBySymbolContains(symbolSelected).getValue();
        }

        BigDecimal secondRate = null;
        if (symbolTarget.equals("EUR")) {
            secondRate = new BigDecimal(1);
        } else {
            secondRate = this.currentFxRatesRepo.findBySymbolContains(symbolTarget).getValue();
        }

        BigDecimal amount = new BigDecimal(input);

        BigDecimal result = amount.divide(firstRate, 18, RoundingMode.FLOOR).multiply(secondRate);
        BigDecimal resultScaled = result.setScale(2, BigDecimal.ROUND_FLOOR);
        return resultScaled.doubleValue();
    }
}
