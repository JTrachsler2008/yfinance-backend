package ch.allianz.jt.service.impl;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.PositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class PositionServiceImpl implements PositionService {

    private static final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);

    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;
    private final YFinanceClient yFinanceClient;

    public PositionServiceImpl(final PositionRepository positionRepository,
                               final TransactionRepository transactionRepository,
                               final YFinanceClient yFinanceClient) {
        this.positionRepository = positionRepository;
        this.transactionRepository = transactionRepository;
        this.yFinanceClient = yFinanceClient;
    }

    @Override
    public List<Position> getAll() {
        return positionRepository.findAll();
    }

    @Override
    public Position create(final Position position) {
        return positionRepository.save(position);
    }

    @Override
    public List<Position> getByAccountId(final Long accountId) {
        return positionRepository.findByAccountId(accountId);
    }

    /**
     * Baut aus der Transaktionshistorie einer Position die offenen Kauf-Tranchen (Lots) nach dem
     * FIFO-Prinzip auf: Verkäufe verbrauchen zuerst die älteste offene Tranche.
     */
    @Override
    public List<Map<String, Object>> getLotsFifo(final Long accountId, final Long securityId) {
        log.debug("FIFO-Lots berechnen: Account={}, Security={}", accountId, securityId);

        List<Transaction> transactions = transactionRepository.findByAccountIdAndSecurityIdOrderByDate(accountId, securityId);
        LinkedList<Lot> openLots = new LinkedList<>();
        String symbol = null;

        for (Transaction t : transactions) {
            if (t.getSecurity() != null) symbol = t.getSecurity().getSymbol();
            String type = t.getTransactionType().toUpperCase();
            if (t.getQuantity() == null || t.getPrice() == null) continue;

            if (type.equals("BUY") || type.equals("ACQUISITION")) {
                double fees = (t.getFee() != null ? t.getFee() : 0.0) + (t.getTax() != null ? t.getTax() : 0.0);
                openLots.add(new Lot(t.getTransactionDate(), t.getQuantity(), t.getPrice(), fees, t.getQuantity()));
            } else if (type.equals("SELL")) {
                double toSell = t.getQuantity();
                while (toSell > 0.0001 && !openLots.isEmpty()) {
                    Lot oldest = openLots.getFirst();
                    double consume = Math.min(oldest.remainingQuantity, toSell);
                    oldest.remainingQuantity -= consume;
                    toSell -= consume;
                    if (oldest.remainingQuantity <= 0.0001) {
                        openLots.removeFirst();
                    }
                }
                if (toSell > 0.0001) {
                    log.warn("Verkauf übersteigt offene Lots: Account={}, Security={}, überschüssige Menge={}",
                            accountId, securityId, toSell);
                }
            }
        }

        if (openLots.isEmpty()) return List.of();

        BigDecimal currentPrice = null;
        try {
            QuoteResponse quote = yFinanceClient.getQuote(symbol);
            if (quote != null && quote.getCurrentPrice() != null) currentPrice = quote.getCurrentPrice();
        } catch (Exception e) {
            log.warn("Aktueller Kurs für {} konnte nicht geladen werden: {}", symbol, e.getMessage());
        }

        LocalDate earliestPurchase = openLots.stream().map(l -> l.purchaseDate).min(LocalDate::compareTo).orElse(LocalDate.now());
        Map<LocalDate, BigDecimal> monthlyPrices = fetchMonthlyPrices(symbol, earliestPurchase);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Lot lot : openLots) {
            double avgCostPerShare = lot.purchasePrice + (lot.fees / lot.originalQuantity);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("symbol", symbol);
            row.put("kaufdatum", lot.purchaseDate);
            row.put("menge", round(lot.remainingQuantity));
            row.put("kaufpreis", round(lot.purchasePrice));
            row.put("einstandspreisProStueck", round(avgCostPerShare));
            if (currentPrice != null) {
                double marktwert = lot.remainingQuantity * currentPrice.doubleValue();
                double einstand = lot.remainingQuantity * avgCostPerShare;
                double gewinn = marktwert - einstand;
                row.put("aktuellerKurs", round(currentPrice.doubleValue()));
                row.put("marktwert", round(marktwert));
                row.put("gewinnVerlust", round(gewinn));
                row.put("gewinnVerlustProzent", einstand > 0 ? round(gewinn / einstand * 100) : 0);
            }
            row.put("verlauf", buildLotHistory(lot, monthlyPrices));
            result.add(row);
        }
        return result;
    }

    /**
     * Monatlicher Wertverlauf einer Tranche seit ihrem Kaufdatum (indexiert auf 100 am Kauftag),
     * damit sich mehrere Tranchen in einem Chart optisch vergleichen lassen, egal wann sie gekauft wurden.
     */
    private List<Map<String, Object>> buildLotHistory(final Lot lot, final Map<LocalDate, BigDecimal> monthlyPrices) {
        List<Map<String, Object>> history = new ArrayList<>();
        if (monthlyPrices.isEmpty()) return history;

        BigDecimal basePrice = findClosestPrice(monthlyPrices, lot.purchaseDate);
        if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) == 0) return history;

        LocalDate cursor = lot.purchaseDate.withDayOfMonth(1);
        LocalDate today = LocalDate.now().withDayOfMonth(1);
        while (!cursor.isAfter(today)) {
            BigDecimal price = findClosestPrice(monthlyPrices, cursor);
            if (price != null) {
                double marktwert = lot.remainingQuantity * price.doubleValue();
                double indexiert = price.divide(basePrice, 6, RoundingMode.HALF_UP).doubleValue() * 100;
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("date", cursor.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                point.put("marktwert", round(marktwert));
                point.put("indexiert", round(indexiert));
                history.add(point);
            }
            cursor = cursor.plusMonths(1);
        }
        return history;
    }

    private Map<LocalDate, BigDecimal> fetchMonthlyPrices(final String symbol, final LocalDate from) {
        Map<LocalDate, BigDecimal> prices = new TreeMap<>();
        try {
            HistoricalResponse resp = yFinanceClient.getHistorical(symbol, from, LocalDate.now().minusDays(1), "1mo");
            if (resp != null && resp.getPrices() != null) {
                for (HistoricalPrice p : resp.getPrices()) {
                    if (p.getDate() != null && p.getClose() != null) {
                        prices.put(p.getDate().withDayOfMonth(1), p.getClose());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Monatliche Kurse für {} konnten nicht geladen werden: {}", symbol, e.getMessage());
        }
        return prices;
    }

    private BigDecimal findClosestPrice(final Map<LocalDate, BigDecimal> prices, final LocalDate date) {
        if (prices.isEmpty()) return null;
        LocalDate key = date.withDayOfMonth(1);
        if (prices.containsKey(key)) return prices.get(key);
        return prices.entrySet().stream()
                .filter(e -> !e.getKey().isAfter(key))
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                // Kaufdatum liegt vor der ersten verfügbaren Kurshistorie -> ältesten bekannten Preis als Basis nehmen
                .orElseGet(() -> ((Map.Entry<LocalDate, BigDecimal>) prices.entrySet().stream()
                        .min(Map.Entry.comparingByKey()).orElseThrow()).getValue());
    }

    private double round(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static class Lot {
        final LocalDate purchaseDate;
        final double originalQuantity;
        final double purchasePrice;
        final double fees;
        double remainingQuantity;

        Lot(LocalDate purchaseDate, double originalQuantity, double purchasePrice, double fees, double remainingQuantity) {
            this.purchaseDate = purchaseDate;
            this.originalQuantity = originalQuantity;
            this.purchasePrice = purchasePrice;
            this.fees = fees;
            this.remainingQuantity = remainingQuantity;
        }
    }
}
