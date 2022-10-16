package com.lbapp.LBcalc.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class CurrencyDto {
    @JsonProperty("Ccy")
    private String isoCode;
    @JsonProperty("Amt")
    private BigDecimal amount;
}
