package com.lbapp.LBcalc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryFxRate {
    @Column(name = "date")
    private String date;

    @Column(name = "value", precision = 16, scale = 8)
    private BigDecimal value;
}
