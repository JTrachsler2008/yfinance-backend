package ch.allianz.jt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue
    private Long id;

    private Double totalQuantity;
    private BigDecimal averagePurchasePrice;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "security_id")
    private Security security;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Double totalQuantity) { this.totalQuantity = totalQuantity; }

    public BigDecimal getAveragePurchasePrice() { return averagePurchasePrice; }
    public void setAveragePurchasePrice(BigDecimal averagePurchasePrice) { this.averagePurchasePrice = averagePurchasePrice; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public Security getSecurity() { return security; }
    public void setSecurity(Security security) { this.security = security; }
}
