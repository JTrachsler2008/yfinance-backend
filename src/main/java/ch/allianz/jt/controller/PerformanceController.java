package ch.allianz.jt.controller;

import ch.allianz.jt.dto.PortfolioPerformanceDto;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.repository.PositionRepository;
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

    public PerformanceController(PerformanceService performanceService,
                                 TransactionRepository transactionRepository,
                                 PositionRepository positionRepository) {
        this.performanceService = performanceService;
        this.transactionRepository = transactionRepository;
        this.positionRepository = positionRepository;
    }

    @GetMapping("/{id}/performance")
    public PortfolioPerformanceDto getPerformance(@PathVariable Long id,
                                                  @RequestParam(required = false) String currency) {
        return performanceService.getPortfolioPerformance(id, currency);
    }

    @GetMapping("/{id}/history")
    public List<Map<String, Object>> history(@PathVariable Long id,
                                             @RequestParam(defaultValue = "36") int months,
                                             @RequestParam(required = false) String currency) {
        return performanceService.getPortfolioHistory(id, months, currency);
    }

    @GetMapping("/{id}/realized-gains")
    public List<Map<String, Object>> realizedGains(@PathVariable Long id) {
        List<Transaction> txns = transactionRepository.findByPortfolioIdOrderByDate(id);
        List<Map<String, Object>> result = new ArrayList<>();

        // Track running avg buy price per security
        Map<Long, BigDecimal> avgBuyPrice = new HashMap<>();
        Map<Long, Double> totalQty = new HashMap<>();

        for (Transaction t : txns) {
            if (t.getSecurity() == null || t.getPrice() == null) continue;
            Long secId = t.getSecurity().getId();
            String type = t.getTransactionType().toUpperCase();

            if (type.equals("BUY") || type.equals("ACQUISITION")) {
                double prevQty = totalQty.getOrDefault(secId, 0.0);
                BigDecimal prevAvg = avgBuyPrice.getOrDefault(secId, BigDecimal.ZERO);
                double newQty = prevQty + t.getQuantity();
                BigDecimal newCost = prevAvg.multiply(BigDecimal.valueOf(prevQty))
                        .add(BigDecimal.valueOf(t.getPrice()).multiply(BigDecimal.valueOf(t.getQuantity())));
                avgBuyPrice.put(secId, newCost.divide(BigDecimal.valueOf(newQty), 4, RoundingMode.HALF_UP));
                totalQty.put(secId, newQty);

            } else if (type.equals("SELL")) {
                BigDecimal avg = avgBuyPrice.getOrDefault(secId, BigDecimal.ZERO);
                BigDecimal sellPrice = BigDecimal.valueOf(t.getPrice());
                BigDecimal qty = BigDecimal.valueOf(t.getQuantity());
                BigDecimal gain = sellPrice.subtract(avg).multiply(qty).setScale(2, RoundingMode.HALF_UP);
                BigDecimal gainPct = avg.compareTo(BigDecimal.ZERO) != 0
                        ? sellPrice.subtract(avg).divide(avg, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("symbol", t.getSecurity().getSymbol());
                row.put("name", t.getSecurity().getName());
                row.put("date", t.getTransactionDate());
                row.put("quantity", t.getQuantity());
                row.put("sellPrice", sellPrice);
                row.put("avgBuyPrice", avg.setScale(2, RoundingMode.HALF_UP));
                row.put("realizedGain", gain);
                row.put("realizedGainPercent", gainPct);
                result.add(row);

                totalQty.put(secId, Math.max(0, totalQty.getOrDefault(secId, 0.0) - t.getQuantity()));
            }
        }
        return result;
    }
}
