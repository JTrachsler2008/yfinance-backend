package ch.allianz.jt.dto;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioPerformanceDto {

    private Long portfolioId;
    private String portfolioName;
    private String currency;
    private BigDecimal totalMarketValue;
    private BigDecimal totalGainLoss;
    private BigDecimal totalGainLossPercent;
    private List<PositionPerformanceDto> positions;

    public Long getPortfolioId() { return portfolioId; }
    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }

    public String getPortfolioName() { return portfolioName; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getTotalMarketValue() { return totalMarketValue; }
    public void setTotalMarketValue(BigDecimal totalMarketValue) { this.totalMarketValue = totalMarketValue; }

    public BigDecimal getTotalGainLoss() { return totalGainLoss; }
    public void setTotalGainLoss(BigDecimal totalGainLoss) { this.totalGainLoss = totalGainLoss; }

    public BigDecimal getTotalGainLossPercent() { return totalGainLossPercent; }
    public void setTotalGainLossPercent(BigDecimal totalGainLossPercent) { this.totalGainLossPercent = totalGainLossPercent; }

    public List<PositionPerformanceDto> getPositions() { return positions; }
    public void setPositions(List<PositionPerformanceDto> positions) { this.positions = positions; }
}
