package ch.allianz.jt.service;
import ch.allianz.jt.entity.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction createTransaction(final Long accountId, final Transaction transaction);

    List<Transaction> getAll();
}