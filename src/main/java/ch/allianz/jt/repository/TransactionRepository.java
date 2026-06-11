package ch.allianz.jt.repository;

import ch.allianz.jt.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountId(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.portfolio.id = :portfolioId ORDER BY t.transactionDate ASC")
    List<Transaction> findByPortfolioIdOrderByDate(@Param("portfolioId") Long portfolioId);
}

