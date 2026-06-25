package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/compare")
public class CompareController {

    private final YFinanceClient yFinanceClient;

    public CompareController(YFinanceClient yFinanceClient) {
        this.yFinanceClient = yFinanceClient;
    }

    @GetMapping("/asset-classes")
    public Map<String, Object> assetClasses() {
        LocalDate from = LocalDate.now().minusYears(10);
        LocalDate to = LocalDate.now().minusDays(1);

        Map<String, String> assets = new LinkedHashMap<>();
        assets.put("SPY", "Aktien (S&P 500)");
        assets.put("QQQ", "Tech (Nasdaq 100)");
        assets.put("VNQ", "Immobilien (REITs)");
        assets.put("GLD", "Gold");
        assets.put("AGG", "Anleihen");
        assets.put("BTC-USD", "Bitcoin");

        List<String> allDates = new ArrayList<>();
        Map<String, Map<LocalDate, BigDecimal>> priceData = new LinkedHashMap<>();

        for (String symbol : assets.keySet()) {
            try {
                HistoricalResponse resp = yFinanceClient.getHistorical(symbol, from, to, "1mo");
                if (resp != null && resp.getPrices() != null) {
                    Map<LocalDate, BigDecimal> map = new TreeMap<>();
                    for (HistoricalPrice p : resp.getPrices()) {
                        if (p.getDate() != null && p.getClose() != null) {
                            map.put(p.getDate(), p.getClose());
                        }
                    }
                    priceData.put(symbol, map);
                    for (LocalDate d : map.keySet()) {
                        String ds = d.toString();
                        if (!allDates.contains(ds)) allDates.add(ds);
                    }
                }
            } catch (Exception ignored) {}
        }

        Collections.sort(allDates);

        // Normalize to 100 at first data point per asset
        List<Map<String, Object>> chartData = new ArrayList<>();
        Map<String, BigDecimal> baseValues = new HashMap<>();

        for (String dateStr : allDates) {
            LocalDate date = LocalDate.parse(dateStr);
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", dateStr.substring(0, 7)); // YYYY-MM

            boolean hasAny = false;
            for (String symbol : assets.keySet()) {
                Map<LocalDate, BigDecimal> prices = priceData.get(symbol);
                if (prices == null) continue;

                BigDecimal price = findClosest(prices, date);
                if (price == null) continue;

                if (!baseValues.containsKey(symbol)) {
                    baseValues.put(symbol, price);
                }

                BigDecimal base = baseValues.get(symbol);
                if (base.compareTo(BigDecimal.ZERO) == 0) continue;

                BigDecimal normalized = price.divide(base, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
                point.put(symbol, normalized);
                hasAny = true;
            }

            // Cash is always 100 (no return, no loss)
            point.put("CASH", BigDecimal.valueOf(100));
            if (hasAny) chartData.add(point);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("chartData", chartData);
        result.put("assets", buildAssetMeta(assets));
        return result;
    }

    private List<Map<String, String>> buildAssetMeta(Map<String, String> assets) {
        List<Map<String, String>> list = new ArrayList<>();
        for (Map.Entry<String, String> e : assets.entrySet()) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("symbol", e.getKey());
            m.put("label", e.getValue());
            list.add(m);
        }
        Map<String, String> cash = new LinkedHashMap<>();
        cash.put("symbol", "CASH");
        cash.put("label", "Cash");
        list.add(cash);
        return list;
    }

    /**
     * Vergleicht zwei benutzerdefinierte Portfolios.
     * p1 und p2 sind kommagetrennte Listen von SYMBOL:GEWICHT, z.B. "SPY:60,AGG:40"
     */
    @GetMapping("/portfolios")
    public Map<String, Object> comparePortfolios(
            @RequestParam String p1,
            @RequestParam String p2,
            @RequestParam(defaultValue = "Portfolio A") String name1,
            @RequestParam(defaultValue = "Portfolio B") String name2) {

        LocalDate from = LocalDate.now().minusYears(10);
        LocalDate to = LocalDate.now().minusDays(1);

        Map<String, Double> weights1 = parseWeights(p1);
        Map<String, Double> weights2 = parseWeights(p2);

        Set<String> allSymbols = new LinkedHashSet<>();
        allSymbols.addAll(weights1.keySet());
        allSymbols.addAll(weights2.keySet());

        // Lade Preisdaten für alle benötigten Symbole
        Map<String, Map<LocalDate, BigDecimal>> priceData = new LinkedHashMap<>();
        List<String> allDates = new ArrayList<>();

        for (String symbol : allSymbols) {
            try {
                HistoricalResponse resp = yFinanceClient.getHistorical(symbol, from, to, "1mo");
                if (resp != null && resp.getPrices() != null) {
                    Map<LocalDate, BigDecimal> map = new TreeMap<>();
                    for (HistoricalPrice p : resp.getPrices()) {
                        if (p.getDate() != null && p.getClose() != null) {
                            map.put(p.getDate(), p.getClose());
                        }
                    }
                    priceData.put(symbol, map);
                    for (LocalDate d : map.keySet()) {
                        String ds = d.toString();
                        if (!allDates.contains(ds)) allDates.add(ds);
                    }
                }
            } catch (Exception ignored) {}
        }

        Collections.sort(allDates);

        // Normalisiere jeden ETF auf 100 am ersten Punkt
        Map<String, BigDecimal> baseValues = new HashMap<>();
        List<Map<String, Object>> chartData = new ArrayList<>();

        for (String dateStr : allDates) {
            LocalDate date = LocalDate.parse(dateStr);
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", dateStr.substring(0, 7));

            // Normalisierte Werte pro Symbol setzen
            Map<String, BigDecimal> normalizedPrices = new HashMap<>();
            for (String symbol : allSymbols) {
                Map<LocalDate, BigDecimal> prices = priceData.get(symbol);
                if (prices == null) continue;
                BigDecimal price = findClosest(prices, date);
                if (price == null) continue;
                if (!baseValues.containsKey(symbol)) baseValues.put(symbol, price);
                BigDecimal base = baseValues.get(symbol);
                if (base.compareTo(BigDecimal.ZERO) == 0) continue;
                normalizedPrices.put(symbol, price.divide(base, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            }

            // Portfolio-Werte als gewichteter Durchschnitt
            BigDecimal v1 = portfolioValue(weights1, normalizedPrices);
            BigDecimal v2 = portfolioValue(weights2, normalizedPrices);

            if (v1 != null) point.put("p1", v1.setScale(2, RoundingMode.HALF_UP));
            if (v2 != null) point.put("p2", v2.setScale(2, RoundingMode.HALF_UP));

            if (v1 != null || v2 != null) chartData.add(point);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("chartData", chartData);
        result.put("name1", name1);
        result.put("name2", name2);
        result.put("weights1", weights1);
        result.put("weights2", weights2);
        return result;
    }

    private Map<String, Double> parseWeights(String param) {
        Map<String, Double> map = new LinkedHashMap<>();
        if (param == null || param.isBlank()) return map;
        for (String part : param.split(",")) {
            String[] kv = part.trim().split(":");
            if (kv.length == 2) {
                try { map.put(kv[0].trim().toUpperCase(), Double.parseDouble(kv[1].trim())); }
                catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }

    private BigDecimal portfolioValue(Map<String, Double> weights, Map<String, BigDecimal> normalizedPrices) {
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalWeight == 0) return null;
        BigDecimal total = BigDecimal.ZERO;
        boolean hasAny = false;
        for (Map.Entry<String, Double> e : weights.entrySet()) {
            BigDecimal price = normalizedPrices.get(e.getKey());
            if (price == null) continue;
            double w = e.getValue() / totalWeight;
            total = total.add(price.multiply(BigDecimal.valueOf(w)));
            hasAny = true;
        }
        return hasAny ? total : null;
    }

    private BigDecimal findClosest(Map<LocalDate, BigDecimal> prices, LocalDate date) {
        if (prices.containsKey(date)) return prices.get(date);
        BigDecimal result = null;
        LocalDate bestDate = null;
        for (LocalDate d : prices.keySet()) {
            if (!d.isAfter(date)) {
                if (bestDate == null || d.isAfter(bestDate)) {
                    bestDate = d;
                    result = prices.get(d);
                }
            }
        }
        return result;
    }
}
