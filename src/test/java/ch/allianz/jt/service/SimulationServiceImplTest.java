package ch.allianz.jt.service;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.BacktestDto;
import ch.allianz.jt.dto.SimulationDto;
import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.entity.Security;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.service.impl.SimulationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationServiceImplTest {

    @Mock PortfolioRepository portfolioRepository;
    @Mock PositionRepository positionRepository;
    @Mock FxRateRepository fxRateRepository;
    @Mock YFinanceClient yFinanceClient;
    @Mock SecurityService securityService;

    SimulationServiceImpl simulationService;

    private Portfolio portfolio;
    private Security security;

    @BeforeEach
    void setUp() {
        simulationService = new SimulationServiceImpl(
                portfolioRepository, positionRepository, fxRateRepository, yFinanceClient, securityService);

        portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setBaseCurrency("CHF");

        security = new Security();
        security.setId(1L);
        security.setSymbol("NVDA");
        security.setName("NVIDIA Corporation");
        security.setTradingCurrency("CHF");
    }

    @Test
    void simulate_wennPortfolioNichtExistiert_sollFehlerWerfen() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                simulationService.simulate(99L, "NVDA", 10.0));
    }

    @Test
    void simulate_wennKursNichtVerfuegbar_sollFehlerWerfen() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(securityService.lookupOrCreate("NVDA")).thenReturn(security);
        when(yFinanceClient.getQuote("NVDA")).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                simulationService.simulate(1L, "NVDA", 10.0));
    }

    @Test
    void simulate_ohneBestehendePositionen_sollNeueGewichtungHundertProzentSetzen() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(securityService.lookupOrCreate("NVDA")).thenReturn(security);
        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(100.0));
        when(yFinanceClient.getQuote("NVDA")).thenReturn(quote);
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of());

        SimulationDto result = simulationService.simulate(1L, "NVDA", 10.0);

        assertEquals(0, BigDecimal.valueOf(1000.00).compareTo(result.getCost()));
        assertEquals(0, BigDecimal.valueOf(1000.00).compareTo(result.getSimulatedPortfolioValue()));
        assertEquals(1, result.getSimulatedWeights().size());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(result.getSimulatedWeights().get(0).getPercentage()));
    }

    @Test
    void simulate_mitBestehenderPosition_sollGewichtungNeuBerechnen() {
        Security existingSec = new Security();
        existingSec.setId(2L);
        existingSec.setSymbol("AAPL");
        existingSec.setTradingCurrency("CHF");

        Position existing = new Position();
        existing.setTotalQuantity(10.0);
        existing.setSecurity(existingSec);
        existing.setAccount(new Account());

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));
        when(securityService.lookupOrCreate("NVDA")).thenReturn(security);
        QuoteResponse nvdaQuote = new QuoteResponse();
        nvdaQuote.setCurrentPrice(BigDecimal.valueOf(100.0));
        when(yFinanceClient.getQuote("NVDA")).thenReturn(nvdaQuote);

        QuoteResponse aaplQuote = new QuoteResponse();
        aaplQuote.setCurrentPrice(BigDecimal.valueOf(100.0));
        lenient().when(yFinanceClient.getQuote("AAPL")).thenReturn(aaplQuote);
        lenient().when(fxRateRepository.findTopByBaseCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(any(), any(), any()))
                .thenReturn(Optional.empty());

        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(existing));

        SimulationDto result = simulationService.simulate(1L, "NVDA", 10.0);

        // Bestand: 10*100=1000 AAPL, Kauf: 10*100=1000 NVDA -> je 50%
        assertEquals(0, BigDecimal.valueOf(1000.00).compareTo(result.getCurrentPortfolioValue()));
        assertEquals(0, BigDecimal.valueOf(2000.00).compareTo(result.getSimulatedPortfolioValue()));
        assertEquals(2, result.getSimulatedWeights().size());
    }

    @Test
    void backtest_sollGewinnUndRenditeBerechnen() {
        when(securityService.lookupOrCreate("AAPL")).thenReturn(security);
        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(150.0));
        when(yFinanceClient.getQuote("AAPL")).thenReturn(quote);

        HistoricalPrice p1 = new HistoricalPrice();
        p1.setDate(LocalDate.of(2024, 1, 1));
        p1.setTimestamp(OffsetDateTime.now());
        p1.setClose(BigDecimal.valueOf(100.0));
        HistoricalResponse resp = new HistoricalResponse("AAPL", List.of(p1));
        when(yFinanceClient.getHistorical(any(), any(), any(), any())).thenReturn(resp);

        BacktestDto result = simulationService.backtest(1L, "AAPL", 10.0, LocalDate.of(2024, 1, 1));

        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(result.getPriceAtBuy()));
        assertEquals(0, BigDecimal.valueOf(1000.00).compareTo(result.getInvestedAmount()));
        assertEquals(0, BigDecimal.valueOf(1500.00).compareTo(result.getCurrentValue()));
        assertEquals(0, BigDecimal.valueOf(500.00).compareTo(result.getGainLoss()));
        assertEquals(1, result.getPriceHistory().size());
    }

    @Test
    void backtest_ohneHistorischeDaten_sollNullwerteZurueckgeben() {
        when(securityService.lookupOrCreate("XYZ")).thenReturn(security);
        when(yFinanceClient.getQuote("XYZ")).thenReturn(null);
        when(yFinanceClient.getHistorical(any(), any(), any(), any())).thenReturn(null);

        BacktestDto result = simulationService.backtest(1L, "XYZ", 5.0, LocalDate.of(2024, 1, 1));

        assertEquals(0, BigDecimal.ZERO.compareTo(result.getInvestedAmount()));
        assertTrue(result.getPriceHistory().isEmpty());
    }
}
