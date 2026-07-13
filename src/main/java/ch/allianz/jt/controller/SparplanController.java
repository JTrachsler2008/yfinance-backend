package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/simulate")
public class SparplanController {

    private final YFinanceClient yFinanceClient;

    public SparplanController(YFinanceClient yFinanceClient) {
        this.yFinanceClient = yFinanceClient;
    }

    @GetMapping("/sparplan")
    public Map<String, Object> sparplan(
            @RequestParam String startDate,
            @RequestParam double betrag,
            @RequestParam(defaultValue = "1") int intervallMonate,
            @RequestParam String positionen) {

        LocalDate from = LocalDate.parse(startDate);
        LocalDate to = LocalDate.now().minusDays(1);

        Map<String, Double> weights = new LinkedHashMap<>();
        for (String part : positionen.split(",")) {
            String[] kv = part.trim().split(":");
            if (kv.length == 2) {
                weights.put(kv[0].trim().toUpperCase(), Double.parseDouble(kv[1].trim()) / 100.0);
            }
        }
        if (weights.isEmpty()) return Map.of("error", "Keine Positionen angegeben");

        Map<String, Map<LocalDate, BigDecimal>> priceMap = new HashMap<>();
        for (String sym : weights.keySet()) {
            HistoricalResponse resp = yFinanceClient.getHistorical(sym, from, to, "1mo");
            if (resp != null && resp.getPrices() != null) {
                Map<LocalDate, BigDecimal> m = new TreeMap<>();
                for (HistoricalPrice p : resp.getPrices()) {
                    if (p.getClose() != null && p.getDate() != null) {
                        m.put(p.getDate().withDayOfMonth(1), p.getClose());
                    }
                }
                priceMap.put(sym, m);
            }
        }

        Map<String, Double> shares = new HashMap<>();
        double totalInvested = 0.0;
        double peak = 0.0;
        double maxDrawdown = 0.0;

        List<Map<String, Object>> chartData = new ArrayList<>();
        LocalDate cursor = from.withDayOfMonth(1);
        int monthIdx = 0;

        while (!cursor.isAfter(to)) {
            if (monthIdx % intervallMonate == 0) {
                totalInvested += betrag;
                for (Map.Entry<String, Double> entry : weights.entrySet()) {
                    String sym = entry.getKey();
                    double invest = betrag * entry.getValue();
                    BigDecimal price = findPrice(priceMap.getOrDefault(sym, Map.of()), cursor);
                    if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                        shares.merge(sym, invest / price.doubleValue(), Double::sum);
                    }
                }
            }

            double currentValue = 0.0;
            for (Map.Entry<String, Double> entry : shares.entrySet()) {
                BigDecimal price = findPrice(priceMap.getOrDefault(entry.getKey(), Map.of()), cursor);
                if (price != null) currentValue += entry.getValue() * price.doubleValue();
            }

            if (currentValue > peak) peak = currentValue;
            if (peak > 0) {
                double dd = (peak - currentValue) / peak * 100;
                if (dd > maxDrawdown) maxDrawdown = dd;
            }

            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", cursor.format(DateTimeFormatter.ofPattern("yyyy-MM")));
            point.put("portfolioWert", round(currentValue));
            point.put("eingezahlt", round(totalInvested));
            chartData.add(point);

            cursor = cursor.plusMonths(1);
            monthIdx++;
        }

        double endValue = chartData.isEmpty() ? 0 :
                ((Number) chartData.get(chartData.size() - 1).get("portfolioWert")).doubleValue();
        double totalReturn = totalInvested > 0 ? (endValue - totalInvested) / totalInvested * 100 : 0;
        long years = java.time.temporal.ChronoUnit.YEARS.between(from, to);
        double cagr = (years > 0 && totalInvested > 0 && endValue > 0)
                ? (Math.pow(endValue / totalInvested, 1.0 / years) - 1) * 100 : 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("chartData", chartData);
        result.put("endwert", round(endValue));
        result.put("eingezahlt", round(totalInvested));
        result.put("gewinn", round(endValue - totalInvested));
        result.put("gesamtRendite", round(totalReturn));
        result.put("cagr", round(cagr));
        result.put("maxDrawdown", round(maxDrawdown));
        return result;
    }

    private BigDecimal findPrice(Map<LocalDate, BigDecimal> prices, LocalDate date) {
        LocalDate key = date.withDayOfMonth(1);
        if (prices.containsKey(key)) return prices.get(key);
        return prices.entrySet().stream()
                .filter(e -> !e.getKey().isAfter(key))
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private double round(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
