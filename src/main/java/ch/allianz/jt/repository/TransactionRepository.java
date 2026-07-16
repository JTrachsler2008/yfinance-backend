package ch.allianz.jt.repository;

import ch.allianz.jt.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.security.id = :securityId ORDER BY t.transactionDate ASC, t.id ASC")
    List<Transaction> findByAccountIdAndSecurityIdOrderByDate(@Param("accountId") Long accountId, @Param("securityId") Long securityId);

    @Query("SELECT t FROM Transaction t WHERE t.account.portfolio.id = :portfolioId ORDER BY t.transactionDate ASC")
    List<Transaction> findByPortfolioIdOrderByDate(@Param("portfolioId") Long portfolioId);
}

