package ch.allianz.jt.dto;

import java.math.BigDecimal;
import java.util.List;

public class SimulationDto {

    private String symbol;
    private String securityName;
    private BigDecimal currentPrice;
    private Double quantity;
    private BigDecimal cost;
    private BigDecimal currentPortfolioValue;
    private BigDecimal simulatedPortfolioValue;
    private BigDecimal valueChange;
    private BigDecimal returnChangePercent;
    private List<WeightItem> currentWeights;
    private List<WeightItem> simulatedWeights;

    public static class WeightItem {
        private String symbol;
        private BigDecimal value;
        private BigDecimal percentage;

        public WeightItem(String symbol, BigDecimal value, BigDecimal percentage) {
            this.symbol = symbol;
            this.value = value;
            this.percentage = percentage;
        }

        public String getSymbol() { return symbol; }
        public BigDecimal getValue() { return value; }
        public BigDecimal getPercentage() { return percentage; }
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getSecurityName() { return securityName; }
    public void setSecurityName(String securityName) { this.securityName = securityName; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public BigDecimal getCurrentPortfolioValue() { return currentPortfolioValue; }
    public void setCurrentPortfolioValue(BigDecimal currentPortfolioValue) { this.currentPortfolioValue = currentPortfolioValue; }

    public BigDecimal getSimulatedPortfolioValue() { return simulatedPortfolioValue; }
    public void setSimulatedPortfolioValue(BigDecimal simulatedPortfolioValue) { this.simulatedPortfolioValue = simulatedPortfolioValue; }

    public BigDecimal getValueChange() { return valueChange; }
    public void setValueChange(BigDecimal valueChange) { this.valueChange = valueChange; }

    public BigDecimal getReturnChangePercent() { return returnChangePercent; }
    public void setReturnChangePercent(BigDecimal returnChangePercent) { this.returnChangePercent = returnChangePercent; }

    public List<WeightItem> getCurrentWeights() { return currentWeights; }
    public void setCurrentWeights(List<WeightItem> currentWeights) { this.currentWeights = currentWeights; }

    public List<WeightItem> getSimulatedWeights() { return simulatedWeights; }
    public void setSimulatedWeights(List<WeightItem> simulatedWeights) { this.simulatedWeights = simulatedWeights; }
}
