package ch.allianz.jt.service;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.PortfolioPerformanceDto;
import ch.allianz.jt.entity.*;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.AccountRepository;
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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceImplTest {

    @Mock PortfolioRepository portfolioRepository;
    @Mock PositionRepository positionRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock FxRateRepository fxRateRepository;
    @Mock YFinanceClient yFinanceClient;
    @Mock AccountRepository accountRepository;

    PerformanceServiceImpl performanceService;

    private Portfolio portfolio;
    private Security security;
    private Account account;

    @BeforeEach
    void setUp() {
        performanceService = new PerformanceServiceImpl(
                portfolioRepository, positionRepository, transactionRepository, fxRateRepository, yFinanceClient, accountRepository);
        lenient().when(accountRepository.findByPortfolioId(anyLong())).thenReturn(List.of());

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
    void getPortfolioPerformance_sollCashZumGesamtwertAddieren() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(buildPosition(10, 100.0)));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of());

        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(150.0));
        when(yFinanceClient.getQuote("AAPL")).thenReturn(quote);

        Account cashAccount = new Account();
        cashAccount.setCurrency("CHF");
        cashAccount.setCashAmount(2000.0);
        when(accountRepository.findByPortfolioId(1L)).thenReturn(List.of(cashAccount));

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, "CHF");

        assertEquals(0, BigDecimal.valueOf(1500.0).compareTo(result.getTotalMarketValue()));
        assertEquals(0, BigDecimal.valueOf(2000.0).compareTo(result.getTotalCash()));
        assertEquals(0, BigDecimal.valueOf(3500.0).compareTo(result.getTotalPortfolioValue()));
    }

    @Test
    void getPortfolioPerformance_dividendenRenditeSollAufAktuellemKursBerechnetWerden() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(buildPosition(10, 8.0)));

        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(10.0));
        when(yFinanceClient.getQuote("AAPL")).thenReturn(quote);

        Transaction dividend = new Transaction();
        dividend.setTransactionType("DIVIDEND");
        dividend.setSecurity(security);
        dividend.setPrice(3.0);
        dividend.setQuantity(1.0);
        dividend.setTransactionCurrency("CHF");
        dividend.setTransactionDate(LocalDate.now().minusMonths(1));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of(dividend));

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, "CHF");

        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(result.getPositions().get(0).getDividendYield()));
    }

    @Test
    void getPortfolioPerformance_dividendenRenditeSollAeltereDividendenAuchZaehlen() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(buildPosition(10, 8.0)));

        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(10.0));
        when(yFinanceClient.getQuote("AAPL")).thenReturn(quote);

        Transaction dividend = new Transaction();
        dividend.setTransactionType("DIVIDEND");
        dividend.setSecurity(security);
        dividend.setPrice(3.0);
        dividend.setQuantity(1.0);
        dividend.setTransactionCurrency("CHF");
        dividend.setTransactionDate(LocalDate.now().minusYears(3));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of(dividend));

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, "CHF");

        assertEquals(0, BigDecimal.valueOf(3.0).compareTo(result.getPositions().get(0).getDividendYield()));
    }

    @Test
    void getPortfolioPerformance_beiHoehermKursSollRenditeNiedrigerSein() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(buildPosition(10, 8.0)));

        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(11.0));
        when(yFinanceClient.getQuote("AAPL")).thenReturn(quote);

        Transaction dividend = new Transaction();
        dividend.setTransactionType("DIVIDEND");
        dividend.setSecurity(security);
        dividend.setPrice(3.0);
        dividend.setQuantity(1.0);
        dividend.setTransactionCurrency("CHF");
        dividend.setTransactionDate(LocalDate.now().minusMonths(1));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of(dividend));

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, "CHF");

        assertTrue(result.getPositions().get(0).getDividendYield().doubleValue() < 3.0);
    }

    @Test
    void getPortfolioHistory_wennKeineTransaktionen_sollLeereListeZurueckgeben() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of());

        List<Map<String, Object>> result = performanceService.getPortfolioHistory(1L, 12, "CHF", null, null, "monthly");

        assertTrue(result.isEmpty());
    }

    @Test
    void getPortfolioHistory_wennPortfolioNichtExistiert_sollFehlerWerfen() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                performanceService.getPortfolioHistory(99L, 12, "CHF", null, null, "monthly"));
    }

    @Test
    void getPortfolioPerformance_mitTransaktionHeute_sollTwrNichtRueckwaertsLaufen() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(buildPosition(10, 100.0)));

        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(100.0));
        when(yFinanceClient.getQuote("AAPL")).thenReturn(quote);

        LocalDate today = LocalDate.now();
        Transaction buyToday = new Transaction();
        buyToday.setTransactionType("BUY");
        buyToday.setSecurity(security);
        buyToday.setQuantity(10.0);
        buyToday.setPrice(100.0);
        buyToday.setTransactionDate(today);
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of(buyToday));

        List<HistoricalPrice> prices = List.of(
                new HistoricalPrice(today, OffsetDateTime.now(), BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100), 1000L));
        when(yFinanceClient.getHistorical(eq("AAPL"), any(), any(), eq("1d")))
                .thenReturn(new HistoricalResponse("AAPL", prices));

        PortfolioPerformanceDto result = performanceService.getPortfolioPerformance(1L, "CHF");

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getTwr()));
    }

    @Test
    void getPortfolioHistory_beiEngerZeitspanneImGleichenMonat_sollDatenZurueckgeben() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        Transaction buy = new Transaction();
        buy.setTransactionType("BUY");
        buy.setSecurity(security);
        buy.setQuantity(10.0);
        buy.setPrice(100.0);
        buy.setTransactionDate(LocalDate.of(2025, 6, 1));
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of(buy));

        List<HistoricalPrice> prices = List.of(
                new HistoricalPrice(LocalDate.of(2025, 6, 20), OffsetDateTime.now(), BigDecimal.valueOf(110), BigDecimal.valueOf(110), BigDecimal.valueOf(110), BigDecimal.valueOf(110), 1000L));
        when(yFinanceClient.getHistorical(eq("AAPL"), any(), any(), eq("1d")))
                .thenReturn(new HistoricalResponse("AAPL", prices));

        List<Map<String, Object>> result = performanceService.getPortfolioHistory(
                1L, 12, "CHF", "2025-06-10", "2025-06-20", "monthly");

        assertFalse(result.isEmpty());
        assertEquals("2025-06", result.get(0).get("date"));
    }

    @Test
    void getYearlyBreakdown_sollWertveraenderungInklKursgewinnBerechnen() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        LocalDate today = LocalDate.now();
        LocalDate buyDate = today.minusMonths(3);
        LocalDate yearStart = LocalDate.of(buyDate.getYear(), 1, 1);

        Transaction buy = new Transaction();
        buy.setTransactionType("BUY");
        buy.setSecurity(security);
        buy.setQuantity(10.0);
        buy.setPrice(100.0);
        buy.setTransactionDate(buyDate);
        buy.setTransactionCurrency("CHF");
        when(transactionRepository.findByPortfolioIdOrderByDate(1L)).thenReturn(List.of(buy));

        List<HistoricalPrice> prices = new ArrayList<>();
        prices.add(new HistoricalPrice(yearStart, OffsetDateTime.now(), BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100), BigDecimal.valueOf(100), 1000L));
        prices.add(new HistoricalPrice(today, OffsetDateTime.now(), BigDecimal.valueOf(130), BigDecimal.valueOf(130), BigDecimal.valueOf(130), BigDecimal.valueOf(130), 1000L));
        when(yFinanceClient.getHistorical(eq("AAPL"), any(), any(), eq("1d")))
                .thenReturn(new HistoricalResponse("AAPL", prices));

        Map<String, Object> result = performanceService.getYearlyBreakdown(1L, "CHF");

        assertEquals(List.of(buyDate.getYear()), result.get("years"));

        List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("rows");
        assertEquals(1, rows.size());
        assertEquals("AAPL", rows.get(0).get("symbol"));

        Map<String, BigDecimal> byYear = (Map<String, BigDecimal>) rows.get(0).get("byYear");
        assertEquals(0, BigDecimal.valueOf(300.0).compareTo(byYear.get(String.valueOf(buyDate.getYear()))));
    }
}
