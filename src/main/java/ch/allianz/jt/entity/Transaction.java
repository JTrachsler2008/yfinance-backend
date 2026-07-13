package ch.allianz.jt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Double fee;   // Broker-Kommission / Transaktionsgebühr
    private Double tax;   // Stempelsteuer, Transaktionssteuer
    private String transactionCurrency;
    private BigDecimal fxRateToPortfolio;
    private LocalDate transactionDate;

    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnoreProperties({"transactions", "portfolio", "hibernateLazyInitializer"})
    private Account account;

    @ManyToOne
    @JoinColumn(name = "security_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer"})
    private Security security;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getFee() { return fee; }
    public void setFee(Double fee) { this.fee = fee; }

    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }

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
