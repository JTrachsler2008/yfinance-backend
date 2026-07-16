package ch.allianz.jt.service;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.RiskAnalysisDto;
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
import ch.allianz.jt.service.impl.RiskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskServiceImplTest {

    @Mock PortfolioRepository portfolioRepository;
    @Mock PositionRepository positionRepository;
    @Mock FxRateRepository fxRateRepository;
    @Mock YFinanceClient yFinanceClient;

    private HistoricalResponse buildDailyPrices(String symbol, int days, double startPrice) {
        List<HistoricalPrice> prices = new ArrayList<>();
        Random rnd = new Random(42);
        double price = startPrice;
        LocalDate date = LocalDate.now().minusDays(days);
        for (int i = 0; i < days; i++) {
            price *= (1 + (rnd.nextDouble() - 0.5) * 0.02);
            HistoricalPrice p = new HistoricalPrice();
            p.setDate(date);
            p.setTimestamp(OffsetDateTime.now());
            p.setClose(BigDecimal.valueOf(price));
            p.setOpen(BigDecimal.valueOf(price));
            p.setHigh(BigDecimal.valueOf(price));
            p.setLow(BigDecimal.valueOf(price));
            p.setVolume(1000L);
            prices.add(p);
            date = date.plusDays(1);
        }
        return new HistoricalResponse(symbol, prices);
    }

    private Portfolio portfolio() {
        Portfolio p = new Portfolio();
        p.setId(1L);
        p.setName("Test-Portfolio");
        p.setBaseCurrency("CHF");
        return p;
    }

    @Test
    void getRiskAnalysis_wennPortfolioNichtExistiert_sollFehlerWerfen() {
        RiskServiceImpl service = new RiskServiceImpl(portfolioRepository, positionRepository, fxRateRepository, yFinanceClient);
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getRiskAnalysis(99L));
    }

    @Test
    void getRiskAnalysis_ohnePositionen_sollNeutraleWerteZurueckgeben() {
        RiskServiceImpl service = new RiskServiceImpl(portfolioRepository, positionRepository, fxRateRepository, yFinanceClient);
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio()));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of());
        when(yFinanceClient.getHistorical(eq("SPY"), any(), any(), eq("1d")))
                .thenReturn(buildDailyPrices("SPY", 60, 400));

        RiskAnalysisDto result = service.getRiskAnalysis(1L);

        assertEquals("CHF", result.getCurrency());
        assertTrue(result.getSecurities().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getPortfolioVolatility()));
    }

    @Test
    void getRiskAnalysis_mitPosition_sollKennzahlenBerechnen() {
        RiskServiceImpl service = new RiskServiceImpl(portfolioRepository, positionRepository, fxRateRepository, yFinanceClient);

        Security security = new Security();
        security.setId(1L);
        security.setSymbol("AAPL");
        security.setName("Apple Inc.");
        security.setTradingCurrency("CHF");

        Position position = new Position();
        position.setTotalQuantity(10.0);
        position.setAveragePurchasePrice(BigDecimal.valueOf(100.0));
        position.setSecurity(security);
        position.setAccount(new Account());

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio()));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(position));
        when(yFinanceClient.getHistorical(eq("SPY"), any(), any(), eq("1d")))
                .thenReturn(buildDailyPrices("SPY", 60, 400));
        when(yFinanceClient.getHistorical(eq("AAPL"), any(), any(), eq("1d")))
                .thenReturn(buildDailyPrices("AAPL", 60, 150));

        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(180.0));
        when(yFinanceClient.getQuote("AAPL")).thenReturn(quote);

        RiskAnalysisDto result = service.getRiskAnalysis(1L);

        assertEquals(1, result.getSecurities().size());
        assertEquals("AAPL", result.getSecurities().get(0).getSymbol());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(result.getSecurities().get(0).getPortfolioWeight()));
        assertNotNull(result.getPortfolioVolatility());
        assertNotNull(result.getPortfolioBeta());
    }

    @Test
    void getRiskAnalysis_wennKursabrufFehlschlaegt_sollKeinenAbsturzVerursachen() {
        RiskServiceImpl service = new RiskServiceImpl(portfolioRepository, positionRepository, fxRateRepository, yFinanceClient);

        Security security = new Security();
        security.setId(1L);
        security.setSymbol("BROKEN");
        security.setTradingCurrency("CHF");

        Position position = new Position();
        position.setTotalQuantity(5.0);
        position.setAveragePurchasePrice(BigDecimal.valueOf(50.0));
        position.setSecurity(security);
        position.setAccount(new Account());

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio()));
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(position));
        when(yFinanceClient.getHistorical(anyString(), any(), any(), eq("1d")))
                .thenThrow(new RuntimeException("yFinance nicht erreichbar"));
        when(yFinanceClient.getQuote(anyString())).thenThrow(new RuntimeException("yFinance nicht erreichbar"));

        RiskAnalysisDto result = service.getRiskAnalysis(1L);

        // Bei fehlenden Kursdaten wird die Position übersprungen (< 10 Datenpunkte), kein Crash
        assertTrue(result.getSecurities().isEmpty());
    }
}
