package ch.allianz.jt.service;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.exception.InsufficientFundsException;
import ch.allianz.jt.repository.AccountRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    PortfolioRepository portfolioRepository;

    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    void deposit_sollCashErhoehen() {
        Account account = new Account();
        account.setId(1L);
        account.setCashAmount(1000.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Account result = accountService.deposit(1L, 500.0);

        assertEquals(1500.0, result.getCashAmount());
    }

    @Test
    void withdraw_wennNichtGenugGeld_sollFehlerWerfen() {
        Account account = new Account();
        account.setId(1L);
        account.setCashAmount(100.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class, () -> {
            accountService.withdraw(1L, 500.0);
        });
    }

    @Test
    void withdraw_sollCashReduzieren() {
        Account account = new Account();
        account.setId(1L);
        account.setCashAmount(800.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        Account result = accountService.withdraw(1L, 300.0);

        assertEquals(500.0, result.getCashAmount());
    }
}
