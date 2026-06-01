package ch.allianz.jt.entity;


import jakarta.persistence.*;

@Entity
public class Security {

    @Id
    @GeneratedValue
    private Long id;

    private String symbol;
    private String isin;
    private String name;
    private String assetType;
    private String exchangeCode;
    private String tradingCurrency;
    private String countryCode;
}