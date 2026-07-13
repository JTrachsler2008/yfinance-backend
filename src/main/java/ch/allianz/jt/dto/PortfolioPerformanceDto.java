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
    /** Time-Weighted Return in % — misst reine Marktrendite, unabhängig von Einzahlungszeitpunkt */
    private BigDecimal twr;
    /** Money-Weighted Return in % — interner Zinsfuss, berücksichtigt Einzahlungszeitpunkte */
    private BigDecimal mwr;
    private BigDecimal totalDividends;
    private BigDecimal totalReturnInclDividends;
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

    public BigDecimal getTwr() { return twr; }
    public void setTwr(BigDecimal twr) { this.twr = twr; }

    public BigDecimal getMwr() { return mwr; }
    public void setMwr(BigDecimal mwr) { this.mwr = mwr; }

    public BigDecimal getTotalDividends() { return totalDividends; }
    public void setTotalDividends(BigDecimal totalDividends) { this.totalDividends = totalDividends; }

    public BigDecimal getTotalReturnInclDividends() { return totalReturnInclDividends; }
    public void setTotalReturnInclDividends(BigDecimal totalReturnInclDividends) { this.totalReturnInclDividends = totalReturnInclDividends; }

    public List<PositionPerformanceDto> getPositions() { return positions; }
    public void setPositions(List<PositionPerformanceDto> positions) { this.positions = positions; }
}
