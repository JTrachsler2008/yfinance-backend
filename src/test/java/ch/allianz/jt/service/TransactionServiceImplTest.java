package ch.allianz.jt.service;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.entity.*;
import ch.allianz.jt.exception.InsufficientFundsException;
import ch.allianz.jt.repository.*;
import ch.allianz.jt.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock TransactionRepository transactionRepository;
    @Mock PositionRepository positionRepository;
    @Mock AccountRepository accountRepository;
    @Mock PortfolioRepository portfolioRepository;
    @Mock SecurityRepository securityRepository;
    @Mock FxRateRepository fxRateRepository;
    @Mock YFinanceClient yFinanceClient;

    @InjectMocks TransactionServiceImpl transactionService;

    private Account account;
    private Security security;
    private Portfolio portfolio;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setBaseCurrency("CHF");

        account = new Account();
        account.setId(1L);
        account.setCashAmount(10000.0);
        account.setPortfolio(portfolio);
        account.setCurrency("CHF");

        security = new Security();
        security.setId(1L);
        security.setSymbol("SPY");
        security.setTradingCurrency("USD");

        lenient().when(accountRepository.findByIdWithPortfolio(1L)).thenReturn(Optional.of(account));
        lenient().when(securityRepository.findById(1L)).thenReturn(Optional.of(security));
        lenient().when(fxRateRepository.findTopByBaseCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
                any(), any(), any())).thenReturn(Optional.empty());
    }

    @Test
    void kauf_ohneGebuehren_sollCashReduzieren() {
        Transaction tx = buildTx("BUY", 100.0, 10.0, null, null);
        when(positionRepository.findByAccountIdAndSecurityId(1L, 1L)).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transactionService.createTransaction(1L, tx);

        assertEquals(9000.0, account.getCashAmount(), 0.01);
    }

    @Test
    void kauf_mitGebuehren_cashAbzugNurPreisTimesQty() {
        // Fees affect avg purchase price but not cash deduction (broker bills separately)
        Transaction tx = buildTx("BUY", 100.0, 10.0, 9.0, 1.5);
        when(positionRepository.findByAccountIdAndSecurityId(1L, 1L)).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transactionService.createTransaction(1L, tx);

        assertEquals(9000.0, account.getCashAmount(), 0.01);
    }

    @Test
    void kauf_mitGebuehren_sollDurchschnittspreisErhoehen() {
        Transaction tx = buildTx("BUY", 100.0, 10.0, 10.0, 0.0);
        when(positionRepository.findByAccountIdAndSecurityId(1L, 1L)).thenReturn(Optional.empty());
        ArgumentCaptor<Position> posCaptor = ArgumentCaptor.forClass(Position.class);
        when(positionRepository.save(posCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transactionService.createTransaction(1L, tx);

        Position saved = posCaptor.getValue();
        // avgPrice = (100 * 10 + 10) / 10 = 101
        assertEquals(101.0, saved.getAveragePurchasePrice().doubleValue(), 0.01);
    }

    @Test
    void kauf_bestehendePosition_sollDurchschnittspreisKombinieren() {
        Position existing = new Position();
        existing.setTotalQuantity(10.0);
        existing.setAveragePurchasePrice(BigDecimal.valueOf(100.0));
        existing.setSecurity(security);
        existing.setAccount(account);

        when(positionRepository.findByAccountIdAndSecurityId(1L, 1L)).thenReturn(Optional.of(existing));
        when(positionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Buy 10 more at 120 + 10 fee
        Transaction tx = buildTx("BUY", 120.0, 10.0, 10.0, 0.0);
        transactionService.createTransaction(1L, tx);

        // newAvg = (100*10 + 120*10 + 10) / 20 = 2210/20 = 110.5
        assertEquals(110.5, existing.getAveragePurchasePrice().doubleValue(), 0.01);
        assertEquals(20.0, existing.getTotalQuantity(), 0.01);
    }

    @Test
    void kauf_nichtGenugCash_sollFehlerWerfen() {
        account.setCashAmount(50.0);
        Transaction tx = buildTx("BUY", 100.0, 10.0, null, null);

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.createTransaction(1L, tx));
    }

    @Test
    void dividende_sollCashErhoehen() {
        Transaction tx = buildTx("DIVIDEND", 2.0, 100.0, null, null);
        tx.setSecurity(null);
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transactionService.createTransaction(1L, tx);

        assertEquals(10200.0, account.getCashAmount(), 0.01);
    }

    private Transaction buildTx(String type, double price, double qty, Double fee, Double tax) {
        Transaction tx = new Transaction();
        tx.setTransactionType(type);
        tx.setPrice(price);
        tx.setQuantity(qty);
        tx.setFee(fee);
        tx.setTax(tax);
        tx.setTransactionDate(LocalDate.now());
        tx.setSecurity(security);
        tx.setTransactionCurrency("USD");
        tx.setFxRateToPortfolio(BigDecimal.ONE);
        return tx;
    }
}
