package com.lbapp.LBcalc.currency;

import com.lbapp.LBcalc.models.Currency;
import com.lbapp.LBcalc.repos.CurrencyRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CurrencyService {

    private final CurrencyRepo currencyRepo;

    public CurrencyService(CurrencyRepo currencyRepo) {
        this.currencyRepo = currencyRepo;
    }

    public List<Currency> getAllCurrencies() {
        return this.currencyRepo.findAll();
    }

    public void deleteCurrencies(List<Integer> ids) {
        this.currencyRepo.deleteAllById(ids);
    }

    public void saveCurrencies(List<Currency> currencies) {
        this.currencyRepo.saveAllAndFlush(currencies);
    }
}
