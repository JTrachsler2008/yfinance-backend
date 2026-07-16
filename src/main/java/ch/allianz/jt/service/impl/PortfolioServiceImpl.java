package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.User;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.repository.AccountRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.repository.UserRepository;
import ch.allianz.jt.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private static final Logger log = LoggerFactory.getLogger(PortfolioServiceImpl.class);

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PositionRepository positionRepository;

    public PortfolioServiceImpl(final PortfolioRepository portfolioRepository,
                                final UserRepository userRepository,
                                final AccountRepository accountRepository,
                                final TransactionRepository transactionRepository,
                                final PositionRepository positionRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.positionRepository = positionRepository;
    }

    @Override
    public Portfolio createPortfolio(final Long userId, final Portfolio portfolio) {
        log.info("Neues Portfolio erstellen: User={}, Name={}, Währung={}", userId, portfolio.getName(), portfolio.getBaseCurrency());

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Portfolio-Erstellung fehlgeschlagen: User {} nicht gefunden", userId);
                    return new ResourceNotFoundException("User not found: " + userId);
                });

        portfolio.setUser(user);
        Portfolio saved = portfolioRepository.save(portfolio);
        log.info("Portfolio erstellt: ID={}", saved.getId());
        return saved;
    }

    @Override
    public List<Portfolio> getAll() {
        return portfolioRepository.findAll();
    }

    @Override
    public Optional<Portfolio> getById(final Long id) {
        return portfolioRepository.findById(id);
    }

    @Override
    public Portfolio updateCurrency(final Long id, final String currency) {
        log.debug("Basiswährung ändern: Portfolio={}, neue Währung={}", id, currency);
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Währungsänderung fehlgeschlagen: Portfolio {} nicht gefunden", id);
                    return new ResourceNotFoundException("Portfolio not found: " + id);
                });
        portfolio.setBaseCurrency(currency);
        return portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public void deletePortfolio(final Long id) {
        log.warn("Lösche Portfolio {} inkl. aller Konten, Positionen und Transaktionen", id);
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Löschung fehlgeschlagen: Portfolio {} nicht gefunden", id);
                    return new ResourceNotFoundException("Portfolio not found: " + id);
                });

        List<Account> accounts = accountRepository.findByPortfolioId(id);
        for (Account account : accounts) {
            List<?> positions = positionRepository.findByAccountId(account.getId());
            List<?> transactions = transactionRepository.findByAccountId(account.getId());
            log.debug("Konto {} löschen: {} Positionen, {} Transaktionen", account.getId(), positions.size(), transactions.size());
            positionRepository.deleteAll(positionRepository.findByAccountId(account.getId()));
            transactionRepository.deleteAll(transactionRepository.findByAccountId(account.getId()));
        }
        accountRepository.deleteAll(accounts);
        portfolioRepository.delete(portfolio);
        log.info("Portfolio gelöscht: ID={}, Name={}, {} Konten entfernt", id, portfolio.getName(), accounts.size());
    }
}