package ch.allianz.jt.service;

import ch.allianz.jt.entity.Account;
import java.util.List;

public interface AccountService {

    Account createAccount(final Long portfolioId, final Account account);

    List<Account> getAllAccounts();

    Account deposit(Long accountId, Double amount);

    Account withdraw(Long accountId, Double amount);
}