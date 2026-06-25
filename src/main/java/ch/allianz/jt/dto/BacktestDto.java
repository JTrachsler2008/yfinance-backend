package ch.allianz.jt.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BacktestDto {

    private String symbol;
    private String securityName;
    private LocalDate buyDate;
    private Double quantity;
    private BigDecimal priceAtBuy;
    private BigDecimal currentPrice;
    private BigDecimal investedAmount;
    private BigDecimal currentValue;
    private BigDecimal gainLoss;
    private BigDecimal returnPercent;
    private List<ChartPoint> priceHistory;

    public static class ChartPoint {
        private String date;
        private BigDecimal price;
        private BigDecimal portfolioValue;

        public ChartPoint(String date, BigDecimal price, BigDecimal portfolioValue) {
            this.date = date;
            this.price = price;
            this.portfolioValue = portfolioValue;
        }

        public String getDate() { return date; }
        public BigDecimal getPrice() { return price; }
        public BigDecimal getPortfolioValue() { return portfolioValue; }
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getSecurityName() { return securityName; }
    public void setSecurityName(String securityName) { this.securityName = securityName; }

    public LocalDate getBuyDate() { return buyDate; }
    public void setBuyDate(LocalDate buyDate) { this.buyDate = buyDate; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public BigDecimal getPriceAtBuy() { return priceAtBuy; }
    public void setPriceAtBuy(BigDecimal priceAtBuy) { this.priceAtBuy = priceAtBuy; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getInvestedAmount() { return investedAmount; }
    public void setInvestedAmount(BigDecimal investedAmount) { this.investedAmount = investedAmount; }

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public BigDecimal getGainLoss() { return gainLoss; }
    public void setGainLoss(BigDecimal gainLoss) { this.gainLoss = gainLoss; }

    public BigDecimal getReturnPercent() { return returnPercent; }
    public void setReturnPercent(BigDecimal returnPercent) { this.returnPercent = returnPercent; }

    public List<ChartPoint> getPriceHistory() { return priceHistory; }
    public void setPriceHistory(List<ChartPoint> priceHistory) { this.priceHistory = priceHistory; }
}
