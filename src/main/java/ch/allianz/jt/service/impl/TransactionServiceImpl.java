package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.exception.InsufficientFundsException;
import ch.allianz.jt.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));

        final String type = transaction.getTransactionType().toUpperCase();
        final double total = transaction.getPrice() * transaction.getQuantity();

        switch (type) {
            case "BUY":
                // Cash-Check
                if (account.getCashAmount() < total) {
                    throw new InsufficientFundsException(
                        "Nicht genug Cash. Verfügbar: " + account.getCashAmount() + ", Benötigt: " + total);
                }
                account.setCashAmount(account.getCashAmount() - total);
                updatePosition(account, transaction, true);
                break;

            case "SELL":
                // Prüfen ob genug Stücke vorhanden
                if (transaction.getSecurity() != null) {
                    Optional<Position> pos = positionRepository
                            .findByAccountIdAndSecurityId(account.getId(), transaction.getSecurity().getId());
                    if (pos.isEmpty() || pos.get().getTotalQuantity() < transaction.getQuantity()) {
                        throw new InsufficientFundsException("Nicht genug Stücke zum Verkauf vorhanden.");
                    }
                }
                account.setCashAmount(account.getCashAmount() + total);
                updatePosition(account, transaction, false);
                break;

            case "DIVIDEND":
                // Dividende erhöht nur Cash, keine Positionsänderung
                account.setCashAmount(account.getCashAmount() + total);
                break;

            case "SPLIT":
                // Aktien-Split: Stückzahl anpassen
                handleSplit(account, transaction);
                break;

            case "ACQUISITION":
            case "MERGER":
                // Alte Position entfernen, neue Position erstellen
                handleCorporateAction(account, transaction);
                break;

            default:
                throw new RuntimeException("Unbekannter Transaktionstyp: " + type);
        }

        transaction.setAccount(account);
        transaction.setTransactionDate(LocalDate.now());
        accountRepository.save(account);

        return transactionRepository.save(transaction);
    }

    // BUY: Position erstellen oder Durchschnittspreis neu berechnen
    private void updatePosition(final Account account, final Transaction transaction, final boolean isBuy) {
        if (transaction.getSecurity() == null) {
            return;
        }

        Optional<Position> existing = positionRepository
                .findByAccountIdAndSecurityId(account.getId(), transaction.getSecurity().getId());

        if (existing.isPresent()) {
            Position position = existing.get();
            if (isBuy) {
                double oldQty = position.getTotalQuantity();
                double newQty = oldQty + transaction.getQuantity();
                BigDecimal oldCost = position.getAveragePurchasePrice().multiply(BigDecimal.valueOf(oldQty));
                BigDecimal newCost = BigDecimal.valueOf(transaction.getPrice()).multiply(BigDecimal.valueOf(transaction.getQuantity()));
                BigDecimal avgPrice = oldCost.add(newCost).divide(BigDecimal.valueOf(newQty), 4, RoundingMode.HALF_UP);
                position.setTotalQuantity(newQty);
                position.setAveragePurchasePrice(avgPrice);
            } else {
                position.setTotalQuantity(Math.max(position.getTotalQuantity() - transaction.getQuantity(), 0));
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

    // SPLIT: Stückzahl × Ratio, Einstandspreis ÷ Ratio
    // price = Split-Ratio (z.B. 2.0 für 2:1 Split)
    private void handleSplit(final Account account, final Transaction transaction) {
        if (transaction.getSecurity() == null) {
            return;
        }
        double ratio = transaction.getPrice();
        positionRepository.findByAccountIdAndSecurityId(account.getId(), transaction.getSecurity().getId())
                .ifPresent(position -> {
                    position.setTotalQuantity(position.getTotalQuantity() * ratio);
                    position.setAveragePurchasePrice(
                            position.getAveragePurchasePrice()
                                    .divide(BigDecimal.valueOf(ratio), 4, RoundingMode.HALF_UP));
                    positionRepository.save(position);
                });
    }

    // ACQUISITION / MERGER: Alte Security-Position auf neue übertragen
    // quantity = neue Stückzahl, price = neuer Einstandspreis
    private void handleCorporateAction(final Account account, final Transaction transaction) {
        if (transaction.getSecurity() == null) {
            return;
        }
        Optional<Position> existing = positionRepository
                .findByAccountIdAndSecurityId(account.getId(), transaction.getSecurity().getId());

        if (existing.isPresent()) {
            Position position = existing.get();
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
