package ch.allianz.jt.dto;

import java.math.BigDecimal;
import java.util.List;

public class RiskAnalysisDto {

    private Long portfolioId;
    private String portfolioName;
    private String currency;
    private BigDecimal portfolioAnnualizedReturn;
    private BigDecimal portfolioVolatility;
    private BigDecimal portfolioSharpeRatio;
    private BigDecimal portfolioBeta;
    private BigDecimal portfolioMaxDrawdown;
    private BigDecimal portfolioVar95;
    private BigDecimal riskFreeRate;
    private BigDecimal diversificationBenefit;
    private List<SecurityRiskDto> securities;

    public Long getPortfolioId() { return portfolioId; }
    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }

    public String getPortfolioName() { return portfolioName; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getPortfolioAnnualizedReturn() { return portfolioAnnualizedReturn; }
    public void setPortfolioAnnualizedReturn(BigDecimal portfolioAnnualizedReturn) { this.portfolioAnnualizedReturn = portfolioAnnualizedReturn; }

    public BigDecimal getPortfolioVolatility() { return portfolioVolatility; }
    public void setPortfolioVolatility(BigDecimal portfolioVolatility) { this.portfolioVolatility = portfolioVolatility; }

    public BigDecimal getPortfolioSharpeRatio() { return portfolioSharpeRatio; }
    public void setPortfolioSharpeRatio(BigDecimal portfolioSharpeRatio) { this.portfolioSharpeRatio = portfolioSharpeRatio; }

    public BigDecimal getPortfolioBeta() { return portfolioBeta; }
    public void setPortfolioBeta(BigDecimal portfolioBeta) { this.portfolioBeta = portfolioBeta; }

    public BigDecimal getPortfolioMaxDrawdown() { return portfolioMaxDrawdown; }
    public void setPortfolioMaxDrawdown(BigDecimal portfolioMaxDrawdown) { this.portfolioMaxDrawdown = portfolioMaxDrawdown; }

    public BigDecimal getPortfolioVar95() { return portfolioVar95; }
    public void setPortfolioVar95(BigDecimal portfolioVar95) { this.portfolioVar95 = portfolioVar95; }

    public BigDecimal getRiskFreeRate() { return riskFreeRate; }
    public void setRiskFreeRate(BigDecimal riskFreeRate) { this.riskFreeRate = riskFreeRate; }

    public BigDecimal getDiversificationBenefit() { return diversificationBenefit; }
    public void setDiversificationBenefit(BigDecimal diversificationBenefit) { this.diversificationBenefit = diversificationBenefit; }

    public List<SecurityRiskDto> getSecurities() { return securities; }
    public void setSecurities(List<SecurityRiskDto> securities) { this.securities = securities; }
}
