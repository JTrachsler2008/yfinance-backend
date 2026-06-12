package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.repository.AccountRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final PortfolioRepository portfolioRepository;

    public AccountServiceImpl(final AccountRepository accountRepository,
                              final PortfolioRepository portfolioRepository) {
        this.accountRepository = accountRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Account createAccount(final Long portfolioId, final Account account) {
        log.info("Account erstellen für Portfolio {}", portfolioId);

        final Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));

        account.setPortfolio(portfolio);

        return accountRepository.save(account);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> getById(final Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Account deposit(final Long accountId, final Double amount) {
        log.info("Einzahlung: Account={}, Betrag={}", accountId, amount);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
        account.setCashAmount(account.getCashAmount() + amount);
        return accountRepository.save(account);
    }

    @Override
    public Account withdraw(final Long accountId, final Double amount) {
        log.info("Auszahlung: Account={}, Betrag={}", accountId, amount);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
        if (account.getCashAmount() < amount) {
            log.warn("Auszahlung fehlgeschlagen: nicht genug Cash auf Account {}", accountId);
            throw new ch.allianz.jt.exception.InsufficientFundsException(
                    "Nicht genug Cash. Verfügbar: " + account.getCashAmount());
        }
        account.setCashAmount(account.getCashAmount() - amount);
        return accountRepository.save(account);
    }
}