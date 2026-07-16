package ch.allianz.jt.service;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.PortfolioPerformanceDto;
import ch.allianz.jt.entity.*;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.impl.PerformanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceImplTest {

    @Mock PortfolioRepository portfolioRepository;
    @Mock PositionRepository positionRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock FxRateRepository fxRateRepository;
    @Mock YFinanceClient yFinanceClient;

    PerformanceServiceImpl performanceService;

    private Portfolio portfolio;
    private Security security;
    private Account account;

    @BeforeEach
    void setUp() {
        performanceService = new PerformanceServiceImpl(
                portfolioRepository, positionRepository, transactionRepository, fxRateRepository, yFinanceClient);

        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Test-Portfolio");
        portfolio.setBaseCurrency("CHF");

        security = new Security();
        security.setId(1L);
        security.setSymbol("AAPL");
        security.setName("Apple Inc.");
        security.setTradingCurrency("CHF");
        security.setSector("Technology");
        security.setCountryCode("US");

        account = new Account();
        account.setId(1L);
    }

    private Position buildPosition(double qty, double avgPrice) {
        Position p = new Position();
        p.setTotalQuantity(qty);
        p.setAveragePurchasePrice(BigDecimal.valueOf(avgPrice));
        p.setSecurity(security);
        p.setAccount(account);
        return p;
    }

    @Test
    void getPortfolioPerformance_wennPortfolioNichtExistiert_sollFehlerWerfen() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                performanceService.getPortfolioPerformance(99L, "CHF"));
    }

    @Test
    void getPortfolioPerformance_sollMarktwertUndGewinnBerechnen() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(buildPosition(10, 100.0)));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of());

        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(150.0));
        when(yFinanceClient.getQuote("AAPL")).thenReturn(quote);

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, "CHF");

        assertEquals(0, BigDecimal.valueOf(1500.0).compareTo(result.getTotalMarketValue()));
        assertEquals(0, BigDecimal.valueOf(500.0).compareTo(result.getTotalGainLoss()));
        assertEquals(1, result.getPositions().size());
        assertEquals("AAPL", result.getPositions().get(0).getSymbol());
    }

    @Test
    void getPortfolioPerformance_ohneCurrencyParam_sollBasiswaehrungVerwenden() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of());
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of());

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, null);

        assertEquals("CHF", result.getCurrency());
    }

    @Test
    void getPortfolioPerformance_ohnePositionen_sollNullwerteZurueckgeben() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of());
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of());

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, "CHF");

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalMarketValue()));
        assertTrue(result.getPositions().isEmpty());
    }

    @Test
    void getPortfolioPerformance_sollDividendenSummieren() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of());

        Transaction dividend = new Transaction();
        dividend.setTransactionType("DIVIDEND");
        dividend.setPrice(2.0);
        dividend.setQuantity(10.0);
        dividend.setTransactionCurrency("CHF");
        dividend.setTransactionDate(LocalDate.of(2024, 1, 1));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of(dividend));

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, "CHF");

        assertEquals(0, BigDecimal.valueOf(20.0).compareTo(result.getTotalDividends()));
    }

    @Test
    void getPortfolioHistory_wennKeineTransaktionen_sollLeereListeZurueckgeben() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of());

        List<Map<String, Object>> result = performanceService.getPortfolioHistory(1L, 12, "CHF", null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPortfolioHistory_wennPortfolioNichtExistiert_sollFehlerWerfen() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                performanceService.getPortfolioHistory(99L, 12, "CHF", null, null));
    }
}
