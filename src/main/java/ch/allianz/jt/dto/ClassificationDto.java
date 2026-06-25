package ch.allianz.jt.dto;

import java.math.BigDecimal;
import java.util.List;

public class ClassificationDto {

    private Long portfolioId;
    private BigDecimal totalValue;
    private List<ClassificationItem> bySector;
    private List<ClassificationItem> byCountry;
    private List<ClassificationItem> byCurrency;

    public static class ClassificationItem {
        private String label;
        private BigDecimal value;
        private BigDecimal percentage;

        public ClassificationItem(String label, BigDecimal value, BigDecimal percentage) {
            this.label = label;
            this.value = value;
            this.percentage = percentage;
        }

        public String getLabel() { return label; }
        public BigDecimal getValue() { return value; }
        public BigDecimal getPercentage() { return percentage; }
    }

    public Long getPortfolioId() { return portfolioId; }
    public void setPortfolioId(Long portfolioId) { this.portfolioId = portfolioId; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public List<ClassificationItem> getBySector() { return bySector; }
    public void setBySector(List<ClassificationItem> bySector) { this.bySector = bySector; }

    public List<ClassificationItem> getByCountry() { return byCountry; }
    public void setByCountry(List<ClassificationItem> byCountry) { this.byCountry = byCountry; }

    public List<ClassificationItem> getByCurrency() { return byCurrency; }
    public void setByCurrency(List<ClassificationItem> byCurrency) { this.byCurrency = byCurrency; }
}
