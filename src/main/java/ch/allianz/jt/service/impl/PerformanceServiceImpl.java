package ch.allianz.jt.service.impl;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.PortfolioPerformanceDto;
import ch.allianz.jt.dto.PositionPerformanceDto;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.entity.Security;
import ch.allianz.jt.entity.Transaction;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.PerformanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private static final Logger log = LoggerFactory.getLogger(PerformanceServiceImpl.class);

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;
    private final FxRateRepository fxRateRepository;
    private final YFinanceClient yFinanceClient;

    public PerformanceServiceImpl(PortfolioRepository portfolioRepository,
                                  PositionRepository positionRepository,
                                  TransactionRepository transactionRepository,
                                  FxRateRepository fxRateRepository,
                                  YFinanceClient yFinanceClient) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.transactionRepository = transactionRepository;
        this.fxRateRepository = fxRateRepository;
        this.yFinanceClient = yFinanceClient;
    }

    @Override
    public PortfolioPerformanceDto getPortfolioPerformance(Long portfolioId) {
        log.info("Performance berechnen für Portfolio {}", portfolioId);

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));

        List<Position> positions = positionRepository.findByAccountPortfolioId(portfolioId);

        List<PositionPerformanceDto> positionDtos = new ArrayList<>();

        for (Position position : positions) {
            String symbol = position.getSecurity().getSymbol();
            String tradingCurrency = position.getSecurity().getTradingCurrency();
            String portfolioCurrency = portfolio.getBaseCurrency();

            QuoteResponse quote = yFinanceClient.getQuote(symbol);
            BigDecimal currentPrice = BigDecimal.ZERO;
            if (quote != null && quote.getCurrentPrice() != null) {
                currentPrice = quote.getCurrentPrice();
            }

            BigDecimal fxRate = getFxRate(tradingCurrency, portfolioCurrency);
            BigDecimal currentPriceConverted = currentPrice.multiply(fxRate);
            BigDecimal avgPriceConverted = position.getAveragePurchasePrice().multiply(fxRate);
            BigDecimal quantity = BigDecimal.valueOf(position.getTotalQuantity());

            BigDecimal marketValue = currentPriceConverted.multiply(quantity);
            BigDecimal cost = avgPriceConverted.multiply(quantity);
            BigDecimal gainLoss = marketValue.subtract(cost);

            BigDecimal gainLossPercent = BigDecimal.ZERO;
            if (cost.compareTo(BigDecimal.ZERO) != 0) {
                gainLossPercent = gainLoss.divide(cost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
            }

            PositionPerformanceDto dto = new PositionPerformanceDto();
            dto.setSymbol(symbol);
            dto.setSecurityName(position.getSecurity().getName());
            dto.setQuantity(position.getTotalQuantity());
            dto.setAveragePurchasePrice(position.getAveragePurchasePrice());
            dto.setCurrentPrice(currentPrice);
            dto.setMarketValue(marketValue);
            dto.setGainLoss(gainLoss);
            dto.setGainLossPercent(gainLossPercent);
            dto.setSector(position.getSecurity().getSector());
            dto.setCountryCode(position.getSecurity().getCountryCode());
            dto.setTradingCurrency(tradingCurrency);

            positionDtos.add(dto);
        }

        BigDecimal totalMarketValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (PositionPerformanceDto pos : positionDtos) {
            totalMarketValue = totalMarketValue.add(pos.getMarketValue());
            totalCost = totalCost.add(pos.getMarketValue().subtract(pos.getGainLoss()));
        }

        BigDecimal totalGainLoss = totalMarketValue.subtract(totalCost);
        BigDecimal totalGainLossPercent = BigDecimal.ZERO;
        if (totalCost.compareTo(BigDecimal.ZERO) != 0) {
            totalGainLossPercent = totalGainLoss.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        }

        PortfolioPerformanceDto result = new PortfolioPerformanceDto();
        result.setPortfolioId(portfolioId);
        result.setPortfolioName(portfolio.getName());
        result.setCurrency(portfolio.getBaseCurrency());
        result.setTotalMarketValue(totalMarketValue);
        result.setTotalGainLoss(totalGainLoss);
        result.setTotalGainLossPercent(totalGainLossPercent);
        result.setTwr(calculateTwr(portfolioId, portfolio.getBaseCurrency()));
        result.setMwr(calculateMwr(portfolioId, totalMarketValue));
        result.setPositions(positionDtos);

        log.info("Performance berechnet: MarketValue={}, TWR={}%, MWR={}%",
                totalMarketValue, result.getTwr(), result.getMwr());
        return result;
    }

    private BigDecimal calculateTwr(Long portfolioId, String portfolioCurrency) {
        log.debug("TWR berechnen für Portfolio {}", portfolioId);
        List<Transaction> txns = transactionRepository.findByPortfolioIdOrderByDate(portfolioId);
        if (txns.isEmpty()) return BigDecimal.ZERO;


        List<LocalDate> subPeriodDates = new ArrayList<>();
        for (Transaction txn : txns) {
            if (txn.getTransactionDate() != null && !subPeriodDates.contains(txn.getTransactionDate())) {
                subPeriodDates.add(txn.getTransactionDate());
            }
        }
        subPeriodDates.add(LocalDate.now().minusDays(1));


        LocalDate firstDate = subPeriodDates.get(0);
        Map<String, Map<LocalDate, BigDecimal>> historicalPrices = new HashMap<>();
        for (Transaction txn : txns) {
            if (txn.getSecurity() == null) continue;
            String symbol = txn.getSecurity().getSymbol();
            if (!historicalPrices.containsKey(symbol)) {
                historicalPrices.put(symbol, fetchHistoricalPriceMap(symbol, firstDate, LocalDate.now().minusDays(1)));
            }
        }


        Map<Long, Security> securityMap = new HashMap<>();
        for (Transaction txn : txns) {
            if (txn.getSecurity() != null) {
                securityMap.put(txn.getSecurity().getId(), txn.getSecurity());
            }
        }

        Map<Long, Double> holdings = new HashMap<>();
        BigDecimal twrProduct = BigDecimal.ONE;

        for (int i = 0; i < subPeriodDates.size() - 1; i++) {
            LocalDate periodStart = subPeriodDates.get(i);
            LocalDate periodEnd = subPeriodDates.get(i + 1);


            for (Transaction txn : txns) {
                if (!periodStart.equals(txn.getTransactionDate())) continue;
                if (txn.getSecurity() == null) continue;

                Long secId = txn.getSecurity().getId();
                double currentQty = 0.0;
                if (holdings.containsKey(secId)) {
                    currentQty = holdings.get(secId);
                }

                String type = txn.getTransactionType().toUpperCase();
                if (type.equals("BUY") || type.equals("ACQUISITION")) {
                    holdings.put(secId, currentQty + txn.getQuantity());
                } else if (type.equals("SELL")) {
                    holdings.put(secId, Math.max(0, currentQty - txn.getQuantity()));
                } else if (type.equals("SPLIT")) {
                    holdings.put(secId, currentQty * txn.getQuantity());
                }
            }

            BigDecimal vAfter = calculatePortfolioValue(holdings, securityMap, historicalPrices, portfolioCurrency, periodStart);
            BigDecimal vEnd = calculatePortfolioValue(holdings, securityMap, historicalPrices, portfolioCurrency, periodEnd);

            if (vAfter.compareTo(BigDecimal.ZERO) != 0) {
                twrProduct = twrProduct.multiply(vEnd.divide(vAfter, 6, RoundingMode.HALF_UP));
            }
        }

        return twrProduct.subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePortfolioValue(Map<Long, Double> holdings, Map<Long, Security> securityMap,
                                               Map<String, Map<LocalDate, BigDecimal>> historicalPrices,
                                               String portfolioCurrency, LocalDate date) {
        BigDecimal total = BigDecimal.ZERO;

        for (Long secId : holdings.keySet()) {
            double qty = holdings.get(secId);
            if (qty <= 0) continue;

            Security sec = securityMap.get(secId);
            if (sec == null) continue;

            BigDecimal price = findClosestPrice(historicalPrices.get(sec.getSymbol()), date);
            if (price == null) continue;

            BigDecimal fxRate = getFxRate(sec.getTradingCurrency(), portfolioCurrency);
            BigDecimal value = price.multiply(fxRate).multiply(BigDecimal.valueOf(qty));
            total = total.add(value);
        }

        return total;
    }

    private BigDecimal findClosestPrice(Map<LocalDate, BigDecimal> priceMap, LocalDate date) {
        if (priceMap == null || priceMap.isEmpty()) return null;
        if (priceMap.containsKey(date)) return priceMap.get(date);

        BigDecimal result = null;
        LocalDate bestDate = null;

        for (LocalDate d : priceMap.keySet()) {
            if (!d.isAfter(date)) {
                if (bestDate == null || d.isAfter(bestDate)) {
                    bestDate = d;
                    result = priceMap.get(d);
                }
            }
        }

        return result;
    }

    private Map<LocalDate, BigDecimal> fetchHistoricalPriceMap(String symbol, LocalDate from, LocalDate to) {
        Map<LocalDate, BigDecimal> result = new HashMap<>();
        try {
            HistoricalResponse response = yFinanceClient.getHistorical(symbol, from, to, "1d");
            if (response != null && response.getPrices() != null) {
                for (HistoricalPrice p : response.getPrices()) {
                    if (p.getDate() != null && p.getClose() != null) {
                        result.put(p.getDate(), p.getClose());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Historische Preise konnten nicht geladen werden für {}: {}", symbol, e.getMessage());
        }
        return result;
    }

    private BigDecimal calculateMwr(Long portfolioId, BigDecimal terminalMarketValue) {
        log.debug("MWR berechnen für Portfolio {}, Endwert={}", portfolioId, terminalMarketValue);
        List<Transaction> txns = transactionRepository.findByPortfolioIdOrderByDate(portfolioId);
        if (txns.isEmpty()) return BigDecimal.ZERO;


        LocalDate firstDate = txns.get(0).getTransactionDate();
        for (Transaction txn : txns) {
            if (txn.getTransactionDate() != null && txn.getTransactionDate().isBefore(firstDate)) {
                firstDate = txn.getTransactionDate();
            }
        }



        List<double[]> cashFlows = new ArrayList<>();

        for (Transaction txn : txns) {
            if (txn.getSecurity() == null || txn.getTransactionDate() == null) continue;

            long days = ChronoUnit.DAYS.between(firstDate, txn.getTransactionDate());
            double amount = 0.0;
            if (txn.getPrice() != null && txn.getQuantity() != null) {
                amount = txn.getPrice() * txn.getQuantity();
            }

            String type = txn.getTransactionType().toUpperCase();
            if (type.equals("BUY") || type.equals("ACQUISITION")) {
                cashFlows.add(new double[]{days, -amount});
            } else if (type.equals("SELL") || type.equals("DIVIDEND")) {
                cashFlows.add(new double[]{days, amount});
            }
        }

        long totalDays = ChronoUnit.DAYS.between(firstDate, LocalDate.now());
        cashFlows.add(new double[]{totalDays, terminalMarketValue.doubleValue()});

        return BigDecimal.valueOf(solveIrr(cashFlows) * 100).setScale(2, RoundingMode.HALF_UP);
    }

    private double solveIrr(List<double[]> cashFlows) {
        double low = -0.9999;
        double high = 50.0;

        double npvLow = calculateNpv(cashFlows, low);
        double npvHigh = calculateNpv(cashFlows, high);

        if (npvLow * npvHigh > 0) return 0.0;

        for (int i = 0; i < 200; i++) {
            double mid = (low + high) / 2.0;
            double npvMid = calculateNpv(cashFlows, mid);
            if (Math.abs(npvMid) < 0.001) return mid;
            if (npvLow * npvMid < 0) {
                high = mid;
            } else {
                low = mid;
                npvLow = npvMid;
            }
        }
        return (low + high) / 2.0;
    }

    private double calculateNpv(List<double[]> cashFlows, double rate) {
        double npv = 0.0;
        for (double[] cf : cashFlows) {
            npv += cf[1] / Math.pow(1.0 + rate, cf[0] / 365.0);
        }
        return npv;
    }

    @Override
    public List<Map<String, Object>> getPortfolioHistory(Long portfolioId, int months) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        String currency = portfolio.getBaseCurrency();

        List<Transaction> txns = transactionRepository.findByPortfolioIdOrderByDate(portfolioId);
        if (txns.isEmpty()) return List.of();

        LocalDate from = LocalDate.now().minusMonths(months);
        LocalDate to = LocalDate.now().minusDays(1);

        // Fetch historical prices for all securities in range
        Map<String, Map<LocalDate, BigDecimal>> historicalPrices = new HashMap<>();
        Map<Long, Security> securityMap = new HashMap<>();
        for (Transaction t : txns) {
            if (t.getSecurity() == null) continue;
            securityMap.put(t.getSecurity().getId(), t.getSecurity());
            String sym = t.getSecurity().getSymbol();
            if (!historicalPrices.containsKey(sym)) {
                historicalPrices.put(sym, fetchHistoricalPriceMap(sym, from, to));
            }
        }

        // Build month-end dates
        List<LocalDate> monthEnds = new ArrayList<>();
        LocalDate cursor = from.withDayOfMonth(from.lengthOfMonth());
        while (!cursor.isAfter(to)) {
            monthEnds.add(cursor);
            cursor = cursor.plusMonths(1).withDayOfMonth(cursor.plusMonths(1).lengthOfMonth());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Map<Long, Double> holdings = new HashMap<>();

        int txIdx = 0;
        for (LocalDate monthEnd : monthEnds) {
            // Apply all transactions up to this month end
            while (txIdx < txns.size() && !txns.get(txIdx).getTransactionDate().isAfter(monthEnd)) {
                Transaction t = txns.get(txIdx);
                if (t.getSecurity() != null) {
                    Long sid = t.getSecurity().getId();
                    String type = t.getTransactionType().toUpperCase();
                    double qty = holdings.getOrDefault(sid, 0.0);
                    if (type.equals("BUY") || type.equals("ACQUISITION")) holdings.put(sid, qty + t.getQuantity());
                    else if (type.equals("SELL")) holdings.put(sid, Math.max(0, qty - t.getQuantity()));
                    else if (type.equals("SPLIT") && t.getQuantity() != null) holdings.put(sid, qty * t.getQuantity());
                }
                txIdx++;
            }

            BigDecimal value = calculatePortfolioValue(holdings, securityMap, historicalPrices, currency, monthEnd);
            if (value.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> point = new LinkedHashMap<>();
                point.put("date", monthEnd.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                point.put("value", value.setScale(2, RoundingMode.HALF_UP));
                result.add(point);
            }
        }
        return result;
    }

    private BigDecimal getFxRate(String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null || fromCurrency.equalsIgnoreCase(toCurrency)) {
            return BigDecimal.ONE;
        }
        return fxRateRepository
                .findTopByBaseCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
                        fromCurrency, toCurrency, LocalDate.now())
                .map(fx -> fx.getRate())
                .orElse(BigDecimal.ONE);
    }
}
