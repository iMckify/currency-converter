package com.lbapp.LBcalc.currency.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "value", precision = 16, scale = 8)
    private BigDecimal value;

    @Override
    public Currency clone() {
        try {
            return (Currency) super.clone();
        } catch (CloneNotSupportedException ignored) {
            return new Currency(this.getID(), this.getSymbol(), this.getValue());
        }
    }
}
