package ch.allianz.jt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    private String transactionType; // BUY / SELL / SPLIT / ACQUISITION / MERGER
    private Double quantity;
    private Double price;
    private String transactionCurrency;
    private BigDecimal fxRateToPortfolio;
    private LocalDate transactionDate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "security_id")
    private Security security;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getTransactionCurrency() { return transactionCurrency; }
    public void setTransactionCurrency(String transactionCurrency) { this.transactionCurrency = transactionCurrency; }

    public BigDecimal getFxRateToPortfolio() { return fxRateToPortfolio; }
    public void setFxRateToPortfolio(BigDecimal fxRateToPortfolio) { this.fxRateToPortfolio = fxRateToPortfolio; }

    public LocalDate getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public Security getSecurity() { return security; }
    public void setSecurity(Security security) { this.security = security; }
}
