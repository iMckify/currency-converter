package com.lbapp.LBcalc.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class CcyAmt {
    @JsonProperty("Ccy")
    private String currency;
    @JsonProperty("Amt")
    private BigDecimal amount;
}
