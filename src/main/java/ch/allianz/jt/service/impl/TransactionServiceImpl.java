package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.repository.AccountRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final PositionRepository positionRepository;

    public TransactionServiceImpl(final TransactionRepository transactionRepository,
                                  final AccountRepository accountRepository,
                                  final PositionRepository positionRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public Transaction createTransaction(final Long accountId, final Transaction transaction) {

        final Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        final double total = transaction.getPrice() * transaction.getQuantity();

        if ("BUY".equalsIgnoreCase(transaction.getTransactionType())) {
            account.setCashAmount(account.getCashAmount() - total);
            updatePosition(account, transaction, true);
        } else if ("SELL".equalsIgnoreCase(transaction.getTransactionType())) {
            account.setCashAmount(account.getCashAmount() + total);
            updatePosition(account, transaction, false);
        }

        transaction.setAccount(account);
        transaction.setTransactionDate(LocalDate.now());

        return transactionRepository.save(transaction);
    }

    private void updatePosition(final Account account, final Transaction transaction, final boolean isBuy) {
        if (transaction.getSecurity() == null) {
            return;
        }

        Optional<Position> existing = positionRepository
                .findByAccountIdAndSecurityId(account.getId(), transaction.getSecurity().getId());

        if (existing.isPresent()) {
            Position position = existing.get();

            if (isBuy) {
                // Neuer Durchschnittspreis berechnen
                double oldQty = position.getTotalQuantity();
                double newQty = oldQty + transaction.getQuantity();
                BigDecimal oldCost = position.getAveragePurchasePrice()
                        .multiply(BigDecimal.valueOf(oldQty));
                BigDecimal newCost = BigDecimal.valueOf(transaction.getPrice())
                        .multiply(BigDecimal.valueOf(transaction.getQuantity()));
                BigDecimal avgPrice = oldCost.add(newCost)
                        .divide(BigDecimal.valueOf(newQty), 4, RoundingMode.HALF_UP);

                position.setTotalQuantity(newQty);
                position.setAveragePurchasePrice(avgPrice);
            } else {
                double newQty = position.getTotalQuantity() - transaction.getQuantity();
                position.setTotalQuantity(Math.max(newQty, 0));
            }

            positionRepository.save(position);

        } else if (isBuy) {
            Position position = new Position();
            position.setAccount(account);
            position.setSecurity(transaction.getSecurity());
            position.setTotalQuantity(transaction.getQuantity());
            position.setAveragePurchasePrice(BigDecimal.valueOf(transaction.getPrice()));
            positionRepository.save(position);
        }
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }
}
