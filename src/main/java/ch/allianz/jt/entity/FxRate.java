package ch.allianz.jt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class FxRate {

    @Id
    @GeneratedValue
    private Long id;

    private String baseCurrency;
    private String quoteCurrency;
    private LocalDate rateDate;
    private BigDecimal rate;
}