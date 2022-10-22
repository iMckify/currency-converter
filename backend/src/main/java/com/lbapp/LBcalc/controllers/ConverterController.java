package com.lbapp.LBcalc.controllers;

import com.lbapp.LBcalc.services.ConverterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/Converter")
public class ConverterController {

    private final ConverterService converterService;

    public ConverterController(ConverterService converterService) {
        this.converterService = converterService;
    }

    @GetMapping("/{symbolFrom}/{symbolTo}/{amount}")
    public BigDecimal convert(
            @PathVariable(value = "symbolFrom") String symbolFrom,
            @PathVariable(value = "symbolTo") String symbolTo,
            @PathVariable(value = "amount") BigDecimal amount
    ) {
        return converterService.convertAPI(symbolFrom, symbolTo, amount);
    }
}
