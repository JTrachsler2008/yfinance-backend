package ch.allianz.jt.repository;

import ch.allianz.jt.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;


    public interface TransactionRepository extends JpaRepository<Transaction, Long> {}

