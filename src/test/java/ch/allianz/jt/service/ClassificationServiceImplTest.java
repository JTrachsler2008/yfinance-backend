package ch.allianz.jt.service;

import ch.allianz.jt.dto.ClassificationDto;
import ch.allianz.jt.dto.ClassificationDto.ClassificationItem;
import ch.allianz.jt.entity.Account;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.entity.Security;
import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.service.impl.ClassificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassificationServiceImplTest {

    @Mock PortfolioRepository portfolioRepository;
    @Mock PositionRepository positionRepository;
    @Mock FxRateRepository fxRateRepository;
    @Mock YFinanceClient yFinanceClient;

    private Position position(String symbol, String sector, double qty, double price) {
        Security sec = new Security();
        sec.setSymbol(symbol);
        sec.setSector(sector);
        sec.setTradingCurrency("CHF");
        Position pos = new Position();
        pos.setSecurity(sec);
        pos.setTotalQuantity(qty);
        pos.setAccount(new Account());

        QuoteResponse quote = new QuoteResponse();
        quote.setCurrentPrice(BigDecimal.valueOf(price));
        when(yFinanceClient.getQuote(symbol)).thenReturn(quote);
        return pos;
    }

    private ClassificationItem findByLabel(List<ClassificationItem> items, String label) {
        return items.stream().filter(i -> i.getLabel().equals(label)).findFirst().orElseThrow();
    }

    @Test
    void getClassification_mitMehrfachSektor_sollWertAufteilen() {
        ClassificationServiceImpl service = new ClassificationServiceImpl(
                portfolioRepository, positionRepository, fxRateRepository, yFinanceClient);

        Portfolio portfolio = new Portfolio();
        portfolio.setBaseCurrency("CHF");
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        Position googl = position("GOOGL", "Technology, Communication Services", 10, 100.0);
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(googl));

        ClassificationDto result = service.getClassification(1L);

        List<ClassificationItem> bySector = result.getBySector();
        assertEquals(2, bySector.size());
        assertEquals(0, BigDecimal.valueOf(500.0).compareTo(findByLabel(bySector, "Technology").getValue()));
        assertEquals(0, BigDecimal.valueOf(500.0).compareTo(findByLabel(bySector, "Communication Services").getValue()));
    }

    @Test
    void getClassification_ohneSektor_sollUnbekanntVerwenden() {
        ClassificationServiceImpl service = new ClassificationServiceImpl(
                portfolioRepository, positionRepository, fxRateRepository, yFinanceClient);

        Portfolio portfolio = new Portfolio();
        portfolio.setBaseCurrency("CHF");
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        Position aapl = position("AAPL", null, 5, 200.0);
        when(positionRepository.findByAccountPortfolioId(1L)).thenReturn(List.of(aapl));

        ClassificationDto result = service.getClassification(1L);

        assertEquals(1, result.getBySector().size());
        assertEquals("Unbekannt", result.getBySector().get(0).getLabel());
        assertEquals(0, BigDecimal.valueOf(1000.0).compareTo(result.getBySector().get(0).getValue()));
    }
}
