package com.lbapp.LBcalc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistorical {
    @Column(name = "date")
    private String date;

    @Column(name = "value", precision = 16, scale = 8)
    private BigDecimal value;
}
