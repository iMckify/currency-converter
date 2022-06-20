package com.lbapp.LBcalc.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class FxRate {
    @JsonProperty("Tp")
    private String type;
    @JsonProperty("Dt")
    private String date;

    @JsonProperty("CcyAmt")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CcyAmt> rates = new ArrayList<>();
}
