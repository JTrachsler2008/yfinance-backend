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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    // Test 1: Einzahlung addiert den Betrag korrekt
    @Test
    void deposit_shouldIncreaseCashAmount() {
        Account account = new Account();
        account.setCashAmount(1000.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Account result = accountService.deposit(1L, 500.0);

        assertThat(result.getCashAmount()).isEqualTo(1500.0);
    }

    // Test 2: Abhebung mit zu wenig Cash wirft InsufficientFundsException
    @Test
    void withdraw_withInsufficientFunds_shouldThrow() {
        Account account = new Account();
        account.setCashAmount(100.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.withdraw(1L, 500.0))
                .isInstanceOf(InsufficientFundsException.class);
    }
}
