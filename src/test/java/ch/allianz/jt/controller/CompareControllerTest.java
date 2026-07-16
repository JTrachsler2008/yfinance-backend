package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompareControllerTest {

    @Mock
    YFinanceClient yFinanceClient;

    private HistoricalResponse buildMonthlyPrices(String symbol, LocalDate start, double... closes) {
        List<HistoricalPrice> prices = new ArrayList<>();
        LocalDate date = start;
        for (double close : closes) {
            HistoricalPrice p = new HistoricalPrice();
            p.setDate(date);
            p.setTimestamp(OffsetDateTime.now());
            p.setClose(BigDecimal.valueOf(close));
            p.setOpen(BigDecimal.valueOf(close));
            p.setHigh(BigDecimal.valueOf(close));
            p.setLow(BigDecimal.valueOf(close));
            p.setVolume(1000L);
            prices.add(p);
            date = date.plusMonths(1);
        }
        return new HistoricalResponse(symbol, prices);
    }

    @Test
    void assetClasses_sollNormalisierteWerteAufHundertStarten() {
        CompareController controller = new CompareController(yFinanceClient);
        LocalDate start = LocalDate.now().minusYears(10).withDayOfMonth(1);

        when(yFinanceClient.getHistorical(any(), any(), any(), eq("1mo")))
                .thenReturn(buildMonthlyPrices("X", start, 100, 110, 121));

        Map<String, Object> result = controller.assetClasses();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> chartData = (List<Map<String, Object>>) result.get("chartData");
        assertFalse(chartData.isEmpty());
        Map<String, Object> firstPoint = chartData.get(0);
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo((BigDecimal) firstPoint.get("SPY")));
        assertEquals(0, BigDecimal.valueOf(100).compareTo((BigDecimal) firstPoint.get("CASH")));
    }

    @Test
    void assetClasses_sollAssetMetadatenMitCashEnthalten() {
        CompareController controller = new CompareController(yFinanceClient);
        when(yFinanceClient.getHistorical(any(), any(), any(), eq("1mo"))).thenReturn(null);

        Map<String, Object> result = controller.assetClasses();

        @SuppressWarnings("unchecked")
        List<Map<String, String>> assets = (List<Map<String, String>>) result.get("assets");
        assertTrue(assets.stream().anyMatch(a -> "CASH".equals(a.get("symbol"))));
        assertTrue(assets.stream().anyMatch(a -> "SPY".equals(a.get("symbol"))));
    }

    @Test
    void assetClasses_wennYFinanceFehlschlaegt_sollKeinenAbsturzVerursachen() {
        CompareController controller = new CompareController(yFinanceClient);
        when(yFinanceClient.getHistorical(any(), any(), any(), eq("1mo")))
                .thenThrow(new RuntimeException("yFinance down"));

        Map<String, Object> result = controller.assetClasses();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> chartData = (List<Map<String, Object>>) result.get("chartData");
        assertTrue(chartData.isEmpty());
    }

    @Test
    void comparePortfolios_sollGewichtetenWertBerechnen() {
        CompareController controller = new CompareController(yFinanceClient);
        LocalDate start = LocalDate.now().minusYears(10).withDayOfMonth(1);

        when(yFinanceClient.getHistorical(eq("SPY"), any(), any(), eq("1mo")))
                .thenReturn(buildMonthlyPrices("SPY", start, 100, 200));
        when(yFinanceClient.getHistorical(eq("AGG"), any(), any(), eq("1mo")))
                .thenReturn(buildMonthlyPrices("AGG", start, 100, 100));

        Map<String, Object> result = controller.comparePortfolios("SPY:50,AGG:50", "SPY:100", "Mix", "Nur SPY");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> chartData = (List<Map<String, Object>>) result.get("chartData");
        Map<String, Object> lastPoint = chartData.get(chartData.size() - 1);

        // p1: 50% SPY (200) + 50% AGG (100) = 150; p2: 100% SPY = 200
        assertEquals(0, BigDecimal.valueOf(150.00).compareTo((BigDecimal) lastPoint.get("p1")));
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo((BigDecimal) lastPoint.get("p2")));
        assertEquals("Mix", result.get("name1"));
        assertEquals("Nur SPY", result.get("name2"));
    }

    @Test
    void comparePortfolios_mitUngueltigenGewichten_sollLeereWeightsZurueckgeben() {
        CompareController controller = new CompareController(yFinanceClient);

        Map<String, Object> result = controller.comparePortfolios("", "", "A", "B");

        @SuppressWarnings("unchecked")
        Map<String, Double> weights1 = (Map<String, Double>) result.get("weights1");
        assertTrue(weights1.isEmpty());
    }

    @Test
    void riskBenchmarks_sollVolatilitaetUndRenditeBerechnen() {
        CompareController controller = new CompareController(yFinanceClient);
        LocalDate start = LocalDate.now().minusYears(3).withDayOfMonth(1);

        when(yFinanceClient.getHistorical(any(), any(), any(), eq("1mo")))
                .thenReturn(buildMonthlyPrices("X", start, 100, 105, 98, 110, 103, 115, 120));

        List<Map<String, Object>> result = controller.riskBenchmarks();

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(r -> "SPY".equals(r.get("symbol"))));
        Map<String, Object> spy = result.stream().filter(r -> "SPY".equals(r.get("symbol"))).findFirst().get();
        assertNotNull(spy.get("volatility"));
        assertNotNull(spy.get("annualizedReturn"));
    }

    @Test
    void riskBenchmarks_mitZuWenigDatenpunkten_sollBenchmarkUeberspringen() {
        CompareController controller = new CompareController(yFinanceClient);
        LocalDate start = LocalDate.now().minusYears(3).withDayOfMonth(1);

        when(yFinanceClient.getHistorical(any(), any(), any(), eq("1mo")))
                .thenReturn(buildMonthlyPrices("X", start, 100, 105));

        List<Map<String, Object>> result = controller.riskBenchmarks();

        assertTrue(result.isEmpty());
    }

    @Test
    void benchmark_sollAufHundertNormalisieren() {
        CompareController controller = new CompareController(yFinanceClient);
        LocalDate start = LocalDate.now().minusMonths(3).withDayOfMonth(1);

        when(yFinanceClient.getHistorical(eq("SPY"), any(), any(), eq("1mo")))
                .thenReturn(buildMonthlyPrices("SPY", start, 400, 440, 480));

        List<Map<String, Object>> result = controller.benchmark("SPY", 3);

        assertEquals(3, result.size());
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo((BigDecimal) result.get(0).get("normalizedValue")));
        assertEquals(0, BigDecimal.valueOf(120.00).compareTo((BigDecimal) result.get(2).get("normalizedValue")));
    }

    @Test
    void benchmark_wennKeineDatenVorhanden_sollLeereListeZurueckgeben() {
        CompareController controller = new CompareController(yFinanceClient);
        when(yFinanceClient.getHistorical(any(), any(), any(), eq("1mo"))).thenReturn(null);

        List<Map<String, Object>> result = controller.benchmark("UNKNOWN", 12);

        assertTrue(result.isEmpty());
    }
}
