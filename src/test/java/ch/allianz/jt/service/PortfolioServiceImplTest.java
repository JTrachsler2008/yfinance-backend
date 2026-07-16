package ch.allianz.jt.service;

import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.entity.User;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.repository.AccountRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.repository.UserRepository;
import ch.allianz.jt.service.impl.PortfolioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceImplTest {

    @Mock PortfolioRepository portfolioRepository;
    @Mock UserRepository userRepository;
    @Mock AccountRepository accountRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock PositionRepository positionRepository;

    PortfolioServiceImpl portfolioService;

    @BeforeEach
    void setUp() {
        portfolioService = new PortfolioServiceImpl(
                portfolioRepository, userRepository, accountRepository, transactionRepository, positionRepository);
    }

    @Test
    void createPortfolio_sollUserZuweisenUndSpeichern() {
        User user = new User();
        user.setId(1L);
        Portfolio portfolio = new Portfolio();
        portfolio.setName("Test-Portfolio");
        portfolio.setBaseCurrency("CHF");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(portfolioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Portfolio result = portfolioService.createPortfolio(1L, portfolio);

        assertEquals(user, result.getUser());
        assertEquals("Test-Portfolio", result.getName());
    }

    @Test
    void createPortfolio_wennUserNichtExistiert_sollFehlerWerfen() {
        Portfolio portfolio = new Portfolio();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                portfolioService.createPortfolio(99L, portfolio));
    }

    @Test
    void updateCurrency_sollBasiswaehrungAendern() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setBaseCurrency("CHF");

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Portfolio result = portfolioService.updateCurrency(1L, "USD");

        assertEquals("USD", result.getBaseCurrency());
    }

    @Test
    void updateCurrency_wennPortfolioNichtExistiert_sollFehlerWerfen() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                portfolioService.updateCurrency(99L, "USD"));
    }

    @Test
    void deletePortfolio_sollKontenPositionenUndTransaktionenMitloeschen() {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Zu löschen");

        Account account = new Account();
        account.setId(10L);

        Position position = new Position();
        Transaction transaction = new Transaction();

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(accountRepository.findByPortfolioId(1L)).thenReturn(List.of(account));
        when(positionRepository.findByAccountId(10L)).thenReturn(List.of(position));
        when(transactionRepository.findByAccountId(10L)).thenReturn(List.of(transaction));

        portfolioService.deletePortfolio(1L);

        verify(positionRepository).deleteAll(List.of(position));
        verify(transactionRepository).deleteAll(List.of(transaction));
        verify(accountRepository).deleteAll(List.of(account));
        verify(portfolioRepository).delete(portfolio);
    }

    @Test
    void deletePortfolio_wennPortfolioNichtExistiert_sollFehlerWerfen() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                portfolioService.deletePortfolio(99L));

        verifyNoInteractions(accountRepository);
    }

    @Test
    void getAll_sollAlleRepositoryEintraegeZurueckgeben() {
        Portfolio p1 = new Portfolio();
        Portfolio p2 = new Portfolio();
        when(portfolioRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Portfolio> result = portfolioService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void getById_wennNichtGefunden_sollEmptyOptionalZurueckgeben() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Portfolio> result = portfolioService.getById(99L);

        assertTrue(result.isEmpty());
    }
}
