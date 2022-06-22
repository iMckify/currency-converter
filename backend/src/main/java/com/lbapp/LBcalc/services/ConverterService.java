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

//    @Autowired
    private CurrentFxRatesRepo currentFxRatesRepo;

    public ConverterService(CurrentFxRatesRepo currentFxRatesRepo) {
        this.currentFxRatesRepo = currentFxRatesRepo;
    }

    // https://sdw.ecb.europa.eu/curConverter.do
    // 100 USD to AUD
    // 100 / 1,0517 USD * 1,5061 AUD = 143.20623752
    // input / first * second
    public BigDecimal convertAPI(String symbolSelected, String symbolTarget, BigDecimal input) {
        List<CurrentFxRate> allRates = this.currentFxRatesRepo.findAll();

        BigDecimal firstRate = null;
        if (symbolSelected.equals("EUR")) {
            firstRate = BigDecimal.ONE;
        } else {
            firstRate = this.currentFxRatesRepo.findBySymbolContains(symbolSelected).getValue();
        }

        BigDecimal secondRate = null;
        if (symbolTarget.equals("EUR")) {
            secondRate = BigDecimal.ONE;
        } else {
            secondRate = this.currentFxRatesRepo.findBySymbolContains(symbolTarget).getValue();
        }

        logger.info("Converting " + symbolSelected + "/" + symbolTarget);
        return convertBigDec(firstRate, secondRate, input);
    }

    public BigDecimal convertBigDec(BigDecimal firstRate, BigDecimal secondRate, BigDecimal amount) {
        logger.info("firstRate:  " + firstRate);
        logger.info("secondRate: " + secondRate);
        BigDecimal result = amount.divide(firstRate, 18, RoundingMode.FLOOR).multiply(secondRate);
        logger.info("Result:\t" + result.toPlainString());
        BigDecimal resultScaled = result.setScale(18, BigDecimal.ROUND_FLOOR);
        logger.info("Converted:\t" + resultScaled.toPlainString());
        return resultScaled;
    }
}
