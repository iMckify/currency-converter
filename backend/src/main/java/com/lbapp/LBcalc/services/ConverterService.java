package com.lbapp.LBcalc.services;

import com.lbapp.LBcalc.repos.CurrencyRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

import static java.math.BigDecimal.ONE;
import static java.math.MathContext.DECIMAL64;
import static java.math.RoundingMode.FLOOR;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class ConverterService {
    private final CurrencyRepo currencyRepo;

    public ConverterService(CurrencyRepo currencyRepo) {
        this.currencyRepo = currencyRepo;
    }

    // https://sdw.ecb.europa.eu/curConverter.do working converter
    // 100 USD to AUD
    // 100 / 1.0521 EUR/USD * 1.5254 EUR/AUD = 140.6366314989069000
    // input / first * second
    public BigDecimal convertAPI(String symbolSelected, String symbolTarget, BigDecimal input) {
        BigDecimal firstRate = getPrice(symbolSelected);
        BigDecimal secondRate = getPrice(symbolTarget);

        log.info("Converting " + symbolSelected + "/" + symbolTarget);
        return convertBigDec(firstRate, secondRate, input);
    }

    // https://keisan.casio.com/calculator checked with settings: mode Real RAD, digits 18, answer standard, accuracy on
    // Casino never loses:
    //  1)  division is FLOOR, so 17th digit is ignored and dropped
    //  2)  multiplication is DECIMAL64, so 17th digit is ignored and dropped
    // In conclusion, casino wins amount of 17th digit each time
    private BigDecimal convertBigDec(BigDecimal firstRate, BigDecimal secondRate, BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount can not be null");
        log.info("firstRate:  " + firstRate);
        log.info("secondRate: " + secondRate);
        BigDecimal result = amount
                .divide(firstRate, 16, FLOOR)
                .multiply(secondRate, DECIMAL64);
        log.info("Result:\t\t" + result.toPlainString());
        BigDecimal resultScaled = result.setScale(16, FLOOR);
        log.info("Converted:\t" + resultScaled.toPlainString());
        return resultScaled;
    }

    private BigDecimal getPrice(String symbol) {
        requireNonNull(symbol, "Symbol can not be null");
        requireNonNull(symbol.isBlank() ? null : symbol, "Symbol can not be blank");
        if (symbol.equals("EUR")) {
            return ONE;
        }
        return this.currencyRepo.findBySymbolContains(symbol)
                .orElseThrow(() -> new NullPointerException("Symbol can not be null"))
                .getValue();
    }
}
