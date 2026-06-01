package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.repository.AccountRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(final TransactionRepository transactionRepository,
                                  final AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Transaction createTransaction(final Long accountId, final Transaction transaction) {

        final Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        final double total = transaction.getPrice() * transaction.getQuantity();

        if ("BUY".equalsIgnoreCase(transaction.getTransactionType())) {
            account.setCashAmount(account.getCashAmount() - total);
        } else if ("SELL".equalsIgnoreCase(transaction.getTransactionType())) {
            account.setCashAmount(account.getCashAmount() + total);
        }

        transaction.setAccount(account);
        transaction.setTransactionDate(java.time.LocalDate.now());

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
}