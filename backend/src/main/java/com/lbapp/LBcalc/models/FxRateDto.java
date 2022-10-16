package com.lbapp.LBcalc.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class FxRateDto {

    private enum ExchangeRateType {EU, LT}

    @JsonProperty("Tp")
    private ExchangeRateType type;
    @JsonProperty("Dt")
    private String date;

    @JsonProperty("CcyAmt")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CurrencyDto> currencyPairs = new ArrayList<>();
}
