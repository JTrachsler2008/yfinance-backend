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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SparplanControllerTest {

    @Mock
    YFinanceClient yFinanceClient;

    // Startpunkt der Simulation: so viele Monate zurück, wie Testdaten geliefert werden.
    // Die Sparplan-Simulation läuft immer bis "heute", darum muss das Startdatum relativ dazu gewählt werden.
    private LocalDate startDateFor(int monthlyDataPoints) {
        return LocalDate.now().minusMonths(monthlyDataPoints - 1).withDayOfMonth(1);
    }

    private HistoricalResponse buildResponse(String symbol, LocalDate start, double... monthlyCloses) {
        List<HistoricalPrice> prices = new java.util.ArrayList<>();
        LocalDate date = start;
        for (double close : monthlyCloses) {
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
    void sparplan_ohneRebalancing_sollGewichtungDriftenLassen() {
        SparplanController controller = new SparplanController(yFinanceClient);
        LocalDate start = startDateFor(3);

        // AAA verdoppelt sich, BBB bleibt gleich -> ohne Rebalancing driftet die Allokation weg von 50/50
        when(yFinanceClient.getHistorical(eq("AAA"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("AAA", start, 100, 100, 200));
        when(yFinanceClient.getHistorical(eq("BBB"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("BBB", start, 100, 100, 100));

        Map<String, Object> result = controller.sparplan(
                start.toString(), 1000, 1, "AAA:50,BBB:50", false, 12, "intervall", 10);

        @SuppressWarnings("unchecked")
        Map<String, Object> aktuelleAllokation = (Map<String, Object>) result.get("aktuelleAllokation");

        double aaaAnteil = ((Number) aktuelleAllokation.get("AAA")).doubleValue();
        // AAA hat sich verdoppelt -> Anteil sollte deutlich über 50% liegen
        assertTrue(aaaAnteil > 55, "AAA-Anteil sollte über 55% gedriftet sein, war: " + aaaAnteil);
    }

    @Test
    void sparplan_mitRebalancing_sollAufZielgewichtungZurueckfuehren() {
        SparplanController controller = new SparplanController(yFinanceClient);
        LocalDate start = startDateFor(3);

        when(yFinanceClient.getHistorical(eq("AAA"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("AAA", start, 100, 100, 200));
        when(yFinanceClient.getHistorical(eq("BBB"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("BBB", start, 100, 100, 100));

        Map<String, Object> result = controller.sparplan(
                start.toString(), 1000, 1, "AAA:50,BBB:50", true, 2, "intervall", 10);

        assertTrue((Boolean) result.get("rebalancing"));
        assertTrue(((Number) result.get("rebalancingCount")).intValue() >= 1);
    }

    @Test
    void sparplan_ohneGueltigePositionen_sollFehlerZurueckgeben() {
        SparplanController controller = new SparplanController(yFinanceClient);

        Map<String, Object> result = controller.sparplan(
                startDateFor(3).toString(), 1000, 1, "", false, 12, "intervall", 10);

        assertEquals("Keine Positionen angegeben", result.get("error"));
    }

    @Test
    void sparplan_rebalancingSollUnabhaengigVonMonatlicherEinzahlungVerkaufen() {
        // Beweist: Rebalancing schichtet den GESAMTEN Portfoliowert um, nicht nur die monatliche Einzahlung.
        // AAA schiesst von 100 auf 10'000 hoch (100x) -> bei 500 CHF/Monat Einzahlung müsste eine reine
        // "Einzahlung umlenken"-Logik das niemals ausgleichen können. Die echte Umschichtung schon.
        SparplanController controller = new SparplanController(yFinanceClient);
        LocalDate start = startDateFor(3);

        when(yFinanceClient.getHistorical(eq("AAA"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("AAA", start, 100, 100, 10000));
        when(yFinanceClient.getHistorical(eq("BBB"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("BBB", start, 100, 100, 100));

        // intervallMonate=999 -> nur die allererste Einzahlung von 500 CHF findet überhaupt statt
        Map<String, Object> result = controller.sparplan(
                start.toString(), 500, 999, "AAA:50,BBB:50", true, 2, "intervall", 10);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> events = (List<Map<String, Object>>) result.get("rebalancingEvents");
        assertFalse(events.isEmpty(), "Es sollte mindestens ein Rebalancing-Ereignis geben");

        @SuppressWarnings("unchecked")
        Map<String, Object> trades = (Map<String, Object>) events.get(0).get("trades");
        double aaaVerkauf = ((Number) trades.get("AAA")).doubleValue();
        double bbbKauf = ((Number) trades.get("BBB")).doubleValue();

        // AAA muss verkauft werden (negativer Trade-Wert), und zwar um ein Vielfaches der 500 CHF Einzahlung
        assertTrue(aaaVerkauf < -5000, "Verkaufsbetrag sollte weit über der 500 CHF Einzahlung liegen, war: " + aaaVerkauf);
        // BBB wird im Gegenzug gekauft
        assertTrue(bbbKauf > 5000, "Kaufbetrag sollte weit über der 500 CHF Einzahlung liegen, war: " + bbbKauf);
        // Symmetrie: was bei AAA verkauft wird, wird 1:1 bei BBB gekauft (kein Geld verschwindet/entsteht)
        assertEquals(0.0, aaaVerkauf + bbbKauf, 0.5);
    }

    @Test
    void sparplan_imSchwellenModus_sollNurBeiUeberschreitungDesBandsRebalancieren() {
        // Ziel 60/40, Band 10 Punkte -> Toleranzbereich 50-70 / 30-50.
        // AAA steigt moderat (bleibt im Band), erst der grosse Sprung am Ende sollte auslösen.
        SparplanController controller = new SparplanController(yFinanceClient);
        LocalDate start = startDateFor(4);

        when(yFinanceClient.getHistorical(eq("AAA"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("AAA", start, 100, 105, 108, 300));
        when(yFinanceClient.getHistorical(eq("BBB"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("BBB", start, 100, 100, 100, 100));

        Map<String, Object> result = controller.sparplan(
                start.toString(), 1000, 1, "AAA:60,BBB:40", true, 999, "schwelle", 10);

        assertEquals("schwelle", result.get("rebalancingModus"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> events = (List<Map<String, Object>>) result.get("rebalancingEvents");
        assertFalse(events.isEmpty(), "Der grosse Kurssprung sollte ein Schwellen-Rebalancing auslösen");
        assertEquals("schwelle", events.get(0).get("grund"));
    }

    @Test
    void sparplan_imSchwellenModus_sollInnerhalbDesBandsNichtRebalancieren() {
        SparplanController controller = new SparplanController(yFinanceClient);
        LocalDate start = startDateFor(3);

        // Leichte Bewegung, bleibt innerhalb von ±10 Punkten um 60/40
        when(yFinanceClient.getHistorical(eq("AAA"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("AAA", start, 100, 103, 106));
        when(yFinanceClient.getHistorical(eq("BBB"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("BBB", start, 100, 100, 100));

        Map<String, Object> result = controller.sparplan(
                start.toString(), 1000, 1, "AAA:60,BBB:40", true, 999, "schwelle", 10);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> events = (List<Map<String, Object>>) result.get("rebalancingEvents");
        assertTrue(events.isEmpty(), "Innerhalb des Toleranzbands sollte kein Rebalancing stattfinden");
    }

    @Test
    void sparplan_sollEndwertUndEingezahltZurueckgeben() {
        SparplanController controller = new SparplanController(yFinanceClient);
        LocalDate start = startDateFor(3);

        when(yFinanceClient.getHistorical(eq("AAA"), any(), any(), eq("1mo")))
                .thenReturn(buildResponse("AAA", start, 100, 110, 121));

        Map<String, Object> result = controller.sparplan(
                start.toString(), 500, 1, "AAA:100", false, 12, "intervall", 10);

        assertNotNull(result.get("endwert"));
        assertEquals(1500.0, ((Number) result.get("eingezahlt")).doubleValue(), 0.01);
    }
}
