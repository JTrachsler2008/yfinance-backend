package ch.allianz.jt.service;

import ch.allianz.jt.entity.Account;
import java.util.List;
import java.util.Optional;

public interface AccountService {

    Account createAccount(final Long portfolioId, final Account account);

    List<Account> getAllAccounts();

    List<Account> getByPortfolio(Long portfolioId);

    Optional<Account> getById(Long id);

    Account deposit(Long accountId, Double amount);

    Account withdraw(Long accountId, Double amount);
}