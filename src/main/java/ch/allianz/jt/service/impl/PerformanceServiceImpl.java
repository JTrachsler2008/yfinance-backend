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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final TransactionRepository transactionRepository;
    private final FxRateRepository fxRateRepository;
    private final YFinanceClient yFinanceClient;

    public PerformanceServiceImpl(final PortfolioRepository portfolioRepository,
                                   final PositionRepository positionRepository,
                                   final TransactionRepository transactionRepository,
                                   final FxRateRepository fxRateRepository,
                                   final YFinanceClient yFinanceClient) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.transactionRepository = transactionRepository;
        this.fxRateRepository = fxRateRepository;
        this.yFinanceClient = yFinanceClient;
    }

    @Override
    public PortfolioPerformanceDto getPortfolioPerformance(final Long portfolioId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));

        List<Position> positions = positionRepository.findByAccountPortfolioId(portfolioId);

        BigDecimal totalMarketValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        List<PositionPerformanceDto> positionDtos = positions.stream().map(position -> {
            PositionPerformanceDto dto = new PositionPerformanceDto();
            String symbol = position.getSecurity().getSymbol();
            String tradingCurrency = position.getSecurity().getTradingCurrency();
            String portfolioCurrency = portfolio.getBaseCurrency();

            dto.setSymbol(symbol);
            dto.setSecurityName(position.getSecurity().getName());
            dto.setQuantity(position.getTotalQuantity());
            dto.setAveragePurchasePrice(position.getAveragePurchasePrice());

            QuoteResponse quote = yFinanceClient.getQuote(symbol);
            BigDecimal currentPrice = quote != null ? quote.getCurrentPrice() : BigDecimal.ZERO;
            dto.setCurrentPrice(currentPrice);

            BigDecimal fxRate = getFxRate(tradingCurrency, portfolioCurrency);
            BigDecimal currentPriceConverted = currentPrice.multiply(fxRate);
            BigDecimal avgPriceConverted = position.getAveragePurchasePrice().multiply(fxRate);

            BigDecimal quantity = BigDecimal.valueOf(position.getTotalQuantity());
            BigDecimal marketValue = currentPriceConverted.multiply(quantity);
            dto.setMarketValue(marketValue);

            BigDecimal cost = avgPriceConverted.multiply(quantity);
            BigDecimal gainLoss = marketValue.subtract(cost);
            dto.setGainLoss(gainLoss);

            if (cost.compareTo(BigDecimal.ZERO) != 0) {
                dto.setGainLossPercent(gainLoss.divide(cost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)));
            } else {
                dto.setGainLossPercent(BigDecimal.ZERO);
            }

            return dto;
        }).toList();

        for (PositionPerformanceDto pos : positionDtos) {
            totalMarketValue = totalMarketValue.add(pos.getMarketValue());
            totalCost = totalCost.add(pos.getMarketValue().subtract(pos.getGainLoss()));
        }

        BigDecimal totalGainLoss = totalMarketValue.subtract(totalCost);
        BigDecimal totalGainLossPercent = totalCost.compareTo(BigDecimal.ZERO) != 0
                ? totalGainLoss.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

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

        return result;
    }

    private BigDecimal calculateTwr(final Long portfolioId, final String portfolioCurrency) {
        List<Transaction> txns = transactionRepository.findByPortfolioIdOrderByDate(portfolioId);
        if (txns.isEmpty()) return BigDecimal.ZERO;

        List<LocalDate> subPeriodDates = txns.stream()
                .map(Transaction::getTransactionDate)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        subPeriodDates.add(LocalDate.now());

        Set<String> symbols = txns.stream()
                .filter(t -> t.getSecurity() != null)
                .map(t -> t.getSecurity().getSymbol())
                .collect(Collectors.toSet());

        Map<String, Map<LocalDate, BigDecimal>> historicalPrices = new HashMap<>();
        LocalDate firstDate = subPeriodDates.get(0);
        for (String symbol : symbols) {
            historicalPrices.put(symbol, fetchHistoricalPriceMap(symbol, firstDate, LocalDate.now()));
        }

        Map<Long, Double> holdings = new HashMap<>();
        Map<Long, Security> securityMap = txns.stream()
                .filter(t -> t.getSecurity() != null)
                .collect(Collectors.toMap(t -> t.getSecurity().getId(), Transaction::getSecurity, (a, b) -> a));

        BigDecimal twrProduct = BigDecimal.ONE;

        for (int i = 0; i < subPeriodDates.size() - 1; i++) {
            LocalDate periodStart = subPeriodDates.get(i);
            LocalDate periodEnd = subPeriodDates.get(i + 1);

            List<Transaction> txnsOnDate = txns.stream()
                    .filter(t -> periodStart.equals(t.getTransactionDate()))
                    .toList();

            for (Transaction txn : txnsOnDate) {
                if (txn.getSecurity() == null) continue;
                Long secId = txn.getSecurity().getId();
                double currentQty = holdings.getOrDefault(secId, 0.0);
                if ("BUY".equalsIgnoreCase(txn.getTransactionType()) || "ACQUISITION".equalsIgnoreCase(txn.getTransactionType())) {
                    holdings.put(secId, currentQty + txn.getQuantity());
                } else if ("SELL".equalsIgnoreCase(txn.getTransactionType())) {
                    holdings.put(secId, Math.max(0, currentQty - txn.getQuantity()));
                } else if ("SPLIT".equalsIgnoreCase(txn.getTransactionType())) {
                    holdings.put(secId, currentQty * txn.getQuantity());
                }
            }

            BigDecimal vAfter = calculatePortfolioValue(holdings, securityMap, historicalPrices, portfolioCurrency, periodStart);
            BigDecimal vEnd = calculatePortfolioValue(holdings, securityMap, historicalPrices, portfolioCurrency, periodEnd);

            if (vAfter != null && vAfter.compareTo(BigDecimal.ZERO) != 0) {
                twrProduct = twrProduct.multiply(vEnd.divide(vAfter, 6, RoundingMode.HALF_UP));
            }
        }

        return twrProduct.subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePortfolioValue(final Map<Long, Double> holdings, final Map<Long, Security> securityMap,
                                               final Map<String, Map<LocalDate, BigDecimal>> historicalPrices,
                                               final String portfolioCurrency, final LocalDate date) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Double> entry : holdings.entrySet()) {
            if (entry.getValue() <= 0) continue;
            Security sec = securityMap.get(entry.getKey());
            if (sec == null) continue;
            BigDecimal price = findClosestPrice(historicalPrices.get(sec.getSymbol()), date);
            if (price == null) continue;
            BigDecimal fxRate = getFxRate(sec.getTradingCurrency(), portfolioCurrency);
            total = total.add(price.multiply(fxRate).multiply(BigDecimal.valueOf(entry.getValue())));
        }
        return total;
    }

    private BigDecimal findClosestPrice(final Map<LocalDate, BigDecimal> priceMap, final LocalDate date) {
        if (priceMap == null || priceMap.isEmpty()) return null;
        if (priceMap.containsKey(date)) return priceMap.get(date);
        return priceMap.entrySet().stream()
                .filter(e -> !e.getKey().isAfter(date))
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private Map<LocalDate, BigDecimal> fetchHistoricalPriceMap(final String symbol, final LocalDate from, final LocalDate to) {
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
            // yFinance nicht erreichbar
        }
        return result;
    }

    private BigDecimal calculateMwr(final Long portfolioId, final BigDecimal terminalMarketValue) {
        List<Transaction> txns = transactionRepository.findByPortfolioIdOrderByDate(portfolioId);
        if (txns.isEmpty()) return BigDecimal.ZERO;

        LocalDate firstDate = txns.stream()
                .map(Transaction::getTransactionDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(LocalDate.now());

        List<double[]> cashFlows = new ArrayList<>();

        for (Transaction txn : txns) {
            if (txn.getSecurity() == null || txn.getTransactionDate() == null) continue;
            long days = ChronoUnit.DAYS.between(firstDate, txn.getTransactionDate());
            double amount = (txn.getPrice() != null ? txn.getPrice() : 0.0)
                          * (txn.getQuantity() != null ? txn.getQuantity() : 0.0);

            switch (txn.getTransactionType().toUpperCase()) {
                case "BUY":
                case "ACQUISITION":
                    cashFlows.add(new double[]{days, -amount});
                    break;
                case "SELL":
                case "DIVIDEND":
                    cashFlows.add(new double[]{days, +amount});
                    break;
                default:
                    break;
            }
        }

        long totalDays = ChronoUnit.DAYS.between(firstDate, LocalDate.now());
        cashFlows.add(new double[]{totalDays, terminalMarketValue.doubleValue()});

        return BigDecimal.valueOf(solveIrr(cashFlows) * 100).setScale(2, RoundingMode.HALF_UP);
    }

    private double solveIrr(final List<double[]> cashFlows) {
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

    private double calculateNpv(final List<double[]> cashFlows, final double rate) {
        double npv = 0.0;
        for (double[] cf : cashFlows) {
            npv += cf[1] / Math.pow(1.0 + rate, cf[0] / 365.0);
        }
        return npv;
    }

    private BigDecimal getFxRate(final String fromCurrency, final String toCurrency) {
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
