package ch.allianz.jt.service;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.entity.Security;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.impl.PositionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceImplTest {

    @Mock PositionRepository positionRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock YFinanceClient yFinanceClient;

    PositionServiceImpl positionService;

    private Security security() {
        Security s = new Security();
        s.setId(1L);
        s.setSymbol("SPY");
        return s;
    }

    private Transaction buildTx(String type, double qty, double price, LocalDate date) {
        Transaction t = new Transaction();
        t.setTransactionType(type);
        t.setQuantity(qty);
        t.setPrice(price);
        t.setTransactionDate(date);
        t.setSecurity(security());
        return t;
    }

    @Test
    void getLotsFifo_ohneVerkauf_sollAlleKaeufeAlsEigeneLotsZeigen() {
        positionService = new PositionServiceImpl(positionRepository, transactionRepository, yFinanceClient);

        List<Transaction> txs = List.of(
                buildTx("BUY", 10, 100.0, LocalDate.of(2024, 1, 1)),
                buildTx("BUY", 20, 120.0, LocalDate.of(2024, 5, 1))
        );
        when(transactionRepository.findByAccountIdAndSecurityIdOrderByDate(1L, 1L)).thenReturn(txs);
        when(yFinanceClient.getQuote("SPY")).thenReturn(quote(150.0));

        List<Map<String, Object>> lots = positionService.getLotsFifo(1L, 1L);

        assertEquals(2, lots.size());
        assertEquals(10.0, ((Number) lots.get(0).get("menge")).doubleValue(), 0.01);
        assertEquals(20.0, ((Number) lots.get(1).get("menge")).doubleValue(), 0.01);
    }

    @Test
    void getLotsFifo_verkaufSollAeltesteTrancheZuerstVerbrauchen() {
        positionService = new PositionServiceImpl(positionRepository, transactionRepository, yFinanceClient);

        // Kauf 10 am 1.1., Kauf 20 am 1.5., Verkauf 15 -> älteste Tranche (10) komplett weg, zweite auf 15 reduziert
        List<Transaction> txs = List.of(
                buildTx("BUY", 10, 100.0, LocalDate.of(2024, 1, 1)),
                buildTx("BUY", 20, 120.0, LocalDate.of(2024, 5, 1)),
                buildTx("SELL", 15, 140.0, LocalDate.of(2024, 8, 1))
        );
        when(transactionRepository.findByAccountIdAndSecurityIdOrderByDate(1L, 1L)).thenReturn(txs);
        when(yFinanceClient.getQuote("SPY")).thenReturn(quote(150.0));

        List<Map<String, Object>> lots = positionService.getLotsFifo(1L, 1L);

        assertEquals(1, lots.size());
        assertEquals(15.0, ((Number) lots.get(0).get("menge")).doubleValue(), 0.01);
        assertEquals(120.0, ((Number) lots.get(0).get("kaufpreis")).doubleValue(), 0.01);
        assertEquals(LocalDate.of(2024, 5, 1), lots.get(0).get("kaufdatum"));
    }

    @Test
    void getLotsFifo_vollstaendigVerkauft_sollLeereListeZurueckgeben() {
        positionService = new PositionServiceImpl(positionRepository, transactionRepository, yFinanceClient);

        List<Transaction> txs = List.of(
                buildTx("BUY", 10, 100.0, LocalDate.of(2024, 1, 1)),
                buildTx("SELL", 10, 140.0, LocalDate.of(2024, 8, 1))
        );
        when(transactionRepository.findByAccountIdAndSecurityIdOrderByDate(1L, 1L)).thenReturn(txs);

        List<Map<String, Object>> lots = positionService.getLotsFifo(1L, 1L);

        assertTrue(lots.isEmpty());
    }

    @Test
    void getLotsFifo_sollGewinnProLotBerechnen() {
        positionService = new PositionServiceImpl(positionRepository, transactionRepository, yFinanceClient);

        List<Transaction> txs = List.of(
                buildTx("BUY", 10, 100.0, LocalDate.of(2024, 1, 1))
        );
        when(transactionRepository.findByAccountIdAndSecurityIdOrderByDate(1L, 1L)).thenReturn(txs);
        when(yFinanceClient.getQuote("SPY")).thenReturn(quote(150.0));

        List<Map<String, Object>> lots = positionService.getLotsFifo(1L, 1L);

        // 10 Stk, Einstand 100, aktueller Kurs 150 -> Gewinn = (150-100)*10 = 500
        assertEquals(500.0, ((Number) lots.get(0).get("gewinnVerlust")).doubleValue(), 0.01);
        assertEquals(50.0, ((Number) lots.get(0).get("gewinnVerlustProzent")).doubleValue(), 0.01);
    }

    private QuoteResponse quote(double price) {
        QuoteResponse q = new QuoteResponse();
        q.setCurrentPrice(BigDecimal.valueOf(price));
        return q;
    }
}
