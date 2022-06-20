package com.lbapp.LBcalc.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class CcyAmt {
    @JsonProperty("Ccy")
    private String currency;
    @JsonProperty("Amt")
    private double amount;
}
