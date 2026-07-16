package ch.allianz.jt.controller;

import ch.allianz.jt.dto.PortfolioPerformanceDto;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.PerformanceService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@RequestMapping("/portfolios")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final TransactionRepository transactionRepository;
    private final PositionRepository positionRepository;
    private final FxRateRepository fxRateRepository;
    private final PortfolioRepository portfolioRepository;

    public PerformanceController(PerformanceService performanceService,
                                 TransactionRepository transactionRepository,
                                 PositionRepository positionRepository,
                                 FxRateRepository fxRateRepository,
                                 PortfolioRepository portfolioRepository) {
        this.performanceService = performanceService;
        this.transactionRepository = transactionRepository;
        this.positionRepository = positionRepository;
        this.fxRateRepository = fxRateRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @GetMapping("/{id}/performance")
    public PortfolioPerformanceDto getPerformance(@PathVariable Long id,
                                                  @RequestParam(required = false) String currency) {
        return performanceService.getPortfolioPerformance(id, currency);
    }

    @GetMapping("/{id}/history")
    public List<Map<String, Object>> history(@PathVariable Long id,
                                             @RequestParam(defaultValue = "36") int months,
                                             @RequestParam(required = false) String currency,
                                             @RequestParam(required = false) String from,
                                             @RequestParam(required = false) String to) {
        return performanceService.getPortfolioHistory(id, months, currency, from, to);
    }

    @GetMapping("/{id}/realized-gains")
    public List<Map<String, Object>> realizedGains(@PathVariable Long id) {
        List<Transaction> txns = transactionRepository.findByPortfolioIdOrderByDate(id);
        List<Map<String, Object>> result = new ArrayList<>();

        Map<Long, BigDecimal> avgBuyPrice = new HashMap<>();
        Map<Long, Double> totalQty = new HashMap<>();

        for (Transaction t : txns) {
            if (t.getSecurity() == null || t.getPrice() == null) continue;
            Long secId = t.getSecurity().getId();
            String type = t.getTransactionType().toUpperCase();

            BigDecimal fees = BigDecimal.valueOf((t.getFee() != null ? t.getFee() : 0.0)
                    + (t.getTax() != null ? t.getTax() : 0.0));

            if (type.equals("BUY") || type.equals("ACQUISITION")) {
                double prevQty = totalQty.getOrDefault(secId, 0.0);
                BigDecimal prevAvg = avgBuyPrice.getOrDefault(secId, BigDecimal.ZERO);
                double newQty = prevQty + t.getQuantity();
                BigDecimal newCost = prevAvg.multiply(BigDecimal.valueOf(prevQty))
                        .add(BigDecimal.valueOf(t.getPrice()).multiply(BigDecimal.valueOf(t.getQuantity())))
                        .add(fees);
                avgBuyPrice.put(secId, newCost.divide(BigDecimal.valueOf(newQty), 4, RoundingMode.HALF_UP));
                totalQty.put(secId, newQty);

            } else if (type.equals("SELL")) {
                BigDecimal avg = avgBuyPrice.getOrDefault(secId, BigDecimal.ZERO);
                BigDecimal sellPrice = BigDecimal.valueOf(t.getPrice());
                BigDecimal qty = BigDecimal.valueOf(t.getQuantity());
                BigDecimal gain = sellPrice.subtract(avg).multiply(qty).subtract(fees).setScale(2, RoundingMode.HALF_UP);
                BigDecimal gainPct = avg.compareTo(BigDecimal.ZERO) != 0
                        ? gain.divide(avg.multiply(qty), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("symbol", t.getSecurity().getSymbol());
                row.put("name", t.getSecurity().getName());
                row.put("date", t.getTransactionDate());
                row.put("quantity", t.getQuantity());
                row.put("sellPrice", sellPrice);
                row.put("avgBuyPrice", avg.setScale(2, RoundingMode.HALF_UP));
                row.put("fee", t.getFee() != null ? t.getFee() : 0.0);
                row.put("tax", t.getTax() != null ? t.getTax() : 0.0);
                row.put("realizedGain", gain);
                row.put("realizedGainPercent", gainPct);
                result.add(row);

                totalQty.put(secId, Math.max(0, totalQty.getOrDefault(secId, 0.0) - t.getQuantity()));
            }
        }
        return result;
    }

    @GetMapping("/{id}/dividends")
    public List<Map<String, Object>> dividends(@PathVariable Long id,
                                               @RequestParam(required = false) String currency) {
        String portfolioCurrency = currency;
        if (portfolioCurrency == null || portfolioCurrency.isBlank()) {
            portfolioCurrency = portfolioRepository.findById(id)
                    .map(p -> p.getBaseCurrency()).orElse("CHF");
        }
        final String cur = portfolioCurrency;

        List<Transaction> txns = transactionRepository.findByPortfolioIdOrderByDate(id);
        Map<String, BigDecimal> perYear = new LinkedHashMap<>();

        for (Transaction t : txns) {
            if (!"DIVIDEND".equalsIgnoreCase(t.getTransactionType())) continue;
            if (t.getPrice() == null || t.getQuantity() == null || t.getTransactionDate() == null) continue;

            String year = String.valueOf(t.getTransactionDate().getYear());
            BigDecimal amount = BigDecimal.valueOf(t.getPrice() * t.getQuantity());

            String divCur = t.getTransactionCurrency() != null ? t.getTransactionCurrency() : cur;
            BigDecimal fxRate = BigDecimal.ONE;
            if (!divCur.equalsIgnoreCase(cur)) {
                fxRate = fxRateRepository
                        .findTopByBaseCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
                                divCur, cur, t.getTransactionDate())
                        .map(r -> r.getRate())
                        .orElse(BigDecimal.ONE);
            }

            perYear.merge(year, amount.multiply(fxRate), BigDecimal::add);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        perYear.forEach((year, total) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("year", year);
            row.put("total", total.setScale(2, RoundingMode.HALF_UP));
            result.add(row);
        });
        return result;
    }
}
