package com.lbapp.LBcalc.services;

import com.lbapp.LBcalc.repos.CurrentFxRatesRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Service
public class ConverterService {
    public static final Logger logger = LoggerFactory.getLogger(ConverterService.class);

    private CurrentFxRatesRepo currentFxRatesRepo;

    public ConverterService(CurrentFxRatesRepo currentFxRatesRepo) {
        this.currentFxRatesRepo = currentFxRatesRepo;
    }

    // https://sdw.ecb.europa.eu/curConverter.do working converter
    // 100 USD to AUD
    // 100 / 1.0521 EUR/USD * 1.5254 EUR/AUD = 140.6366314989069000
    // input / first * second
    public BigDecimal convertAPI(String symbolSelected, String symbolTarget, BigDecimal input) {
        BigDecimal firstRate = getPrice(symbolSelected);
        BigDecimal secondRate = getPrice(symbolTarget);

        logger.info("Converting " + symbolSelected + "/" + symbolTarget);
        return convertBigDec(firstRate, secondRate, input);
    }

    // https://keisan.casio.com/calculator checked with settings: mode Real RAD, digits 18, answer standard, accuracy on
    // Casino never loses:
    //  1)  division is FLOOR, so 17th digit is ignored and dropped
    //  2)  multiplication is DECIMAL64, so 17th digit is ignored and dropped
    // In conclusion, casino wins amount of 17th digit each time
    private BigDecimal convertBigDec(BigDecimal firstRate, BigDecimal secondRate, BigDecimal amount) {
        logger.info("firstRate:  " + firstRate);
        logger.info("secondRate: " + secondRate);
        BigDecimal result = amount.divide(firstRate, 16, RoundingMode.FLOOR).multiply(secondRate, MathContext.DECIMAL64);
        logger.info("Result:\t\t" + result.toPlainString());
        BigDecimal resultScaled = result.setScale(16, RoundingMode.FLOOR);
        logger.info("Converted:\t" + resultScaled.toPlainString());
        return resultScaled;
    }

    private BigDecimal getPrice(String symbol) {
        BigDecimal bigDecimal = null;
        if (symbol == null) {
            throw new IllegalArgumentException("Bad symbol");
        } else if (symbol.equals("EUR")) {
            bigDecimal = BigDecimal.ONE;
        } else {
            bigDecimal = this.currentFxRatesRepo.findBySymbolContains(symbol).orElseThrow(() -> new IllegalArgumentException("Bad symbol")).getValue();
        }
        return bigDecimal;
    }
}
