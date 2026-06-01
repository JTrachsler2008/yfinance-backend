package ch.allianz.jt.service;

import ch.allianz.jt.entity.Account;
import java.util.List;

public interface AccountService {

    Account createAccount(final Long portfolioId, final Account account);

    List<Account> getAllAccounts();
}