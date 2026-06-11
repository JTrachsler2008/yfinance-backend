package ch.allianz.jt.dto;

import java.math.BigDecimal;

public class SecurityRiskDto {

    private String symbol;
    private String securityName;
    private BigDecimal annualizedReturn;
    private BigDecimal volatility;
    private BigDecimal sharpeRatio;
    private BigDecimal beta;
    private BigDecimal maxDrawdown;
    private BigDecimal var95;
    private BigDecimal portfolioWeight;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getSecurityName() { return securityName; }
    public void setSecurityName(String securityName) { this.securityName = securityName; }

    public BigDecimal getAnnualizedReturn() { return annualizedReturn; }
    public void setAnnualizedReturn(BigDecimal annualizedReturn) { this.annualizedReturn = annualizedReturn; }

    public BigDecimal getVolatility() { return volatility; }
    public void setVolatility(BigDecimal volatility) { this.volatility = volatility; }

    public BigDecimal getSharpeRatio() { return sharpeRatio; }
    public void setSharpeRatio(BigDecimal sharpeRatio) { this.sharpeRatio = sharpeRatio; }

    public BigDecimal getBeta() { return beta; }
    public void setBeta(BigDecimal beta) { this.beta = beta; }

    public BigDecimal getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(BigDecimal maxDrawdown) { this.maxDrawdown = maxDrawdown; }

    public BigDecimal getVar95() { return var95; }
    public void setVar95(BigDecimal var95) { this.var95 = var95; }

    public BigDecimal getPortfolioWeight() { return portfolioWeight; }
    public void setPortfolioWeight(BigDecimal portfolioWeight) { this.portfolioWeight = portfolioWeight; }
}
