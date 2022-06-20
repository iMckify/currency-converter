package com.lbapp.LBcalc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentFxRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "value", precision = 16, scale = 8)
    private BigDecimal value;
}
