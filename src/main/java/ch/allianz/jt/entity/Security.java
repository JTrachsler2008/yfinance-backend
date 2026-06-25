package ch.allianz.jt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "securities")
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
    private String sector;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getIsin() { return isin; }
    public void setIsin(String isin) { this.isin = isin; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }

    public String getExchangeCode() { return exchangeCode; }
    public void setExchangeCode(String exchangeCode) { this.exchangeCode = exchangeCode; }

    public String getTradingCurrency() { return tradingCurrency; }
    public void setTradingCurrency(String tradingCurrency) { this.tradingCurrency = tradingCurrency; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
