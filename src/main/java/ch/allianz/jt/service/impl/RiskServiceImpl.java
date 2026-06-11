package ch.allianz.jt.service.impl;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.RiskAnalysisDto;
import ch.allianz.jt.dto.SecurityRiskDto;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.service.RiskService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RiskServiceImpl implements RiskService {

    private static final double RISK_FREE_RATE = 0.04;
    private static final String BENCHMARK_SYMBOL = "SPY";
    private static final double TRADING_DAYS_PER_YEAR = 252.0;
    private static final int LOOKBACK_DAYS = 365;

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final FxRateRepository fxRateRepository;
    private final YFinanceClient yFinanceClient;

    public RiskServiceImpl(final PortfolioRepository portfolioRepository,
                           final PositionRepository positionRepository,
                           final FxRateRepository fxRateRepository,
                           final YFinanceClient yFinanceClient) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.fxRateRepository = fxRateRepository;
        this.yFinanceClient = yFinanceClient;
    }

    @Override
    public RiskAnalysisDto getRiskAnalysis(final Long portfolioId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));

        List<Position> positions = positionRepository.findByAccountPortfolioId(portfolioId);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(LOOKBACK_DAYS);

        List<Double> benchmarkReturns = getDailyReturns(BENCHMARK_SYMBOL, startDate, endDate);

        BigDecimal totalMarketValue = positions.stream()
                .map(p -> getCurrentValue(p, portfolio.getBaseCurrency()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SecurityRiskDto> securityRisks = new ArrayList<>();
        List<Double> weightedVolatilities = new ArrayList<>();

        for (Position position : positions) {
            String symbol = position.getSecurity().getSymbol();

            List<Double> returns = getDailyReturns(symbol, startDate, endDate);
            if (returns.size() < 10) continue;

            double volatility = annualizedVolatility(returns);
            double annReturn = annualizedReturn(returns);
            double sharpe = sharpeRatio(annReturn, volatility);
            double beta = calculateBeta(returns, benchmarkReturns);
            double maxDD = maxDrawdown(symbol, startDate, endDate);
            double var95 = valueAtRisk95(returns);

            BigDecimal posValue = getCurrentValue(position, portfolio.getBaseCurrency());
            BigDecimal weight = totalMarketValue.compareTo(BigDecimal.ZERO) != 0
                    ? posValue.divide(totalMarketValue, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            SecurityRiskDto dto = new SecurityRiskDto();
            dto.setSymbol(symbol);
            dto.setSecurityName(position.getSecurity().getName());
            dto.setAnnualizedReturn(round(annReturn * 100));
            dto.setVolatility(round(volatility * 100));
            dto.setSharpeRatio(round(sharpe));
            dto.setBeta(round(beta));
            dto.setMaxDrawdown(round(maxDD * 100));
            dto.setVar95(round(var95 * 100));
            dto.setPortfolioWeight(weight.setScale(2, RoundingMode.HALF_UP));
            securityRisks.add(dto);

            weightedVolatilities.add((weight.doubleValue() / 100.0) * volatility);
        }

        List<Double> portfolioReturns = calculatePortfolioReturns(positions, portfolio.getBaseCurrency(),
                startDate, endDate, totalMarketValue);

        double portVolatility = annualizedVolatility(portfolioReturns);
        double portReturn = annualizedReturn(portfolioReturns);
        double weightedSumVola = weightedVolatilities.stream().mapToDouble(Double::doubleValue).sum();

        RiskAnalysisDto result = new RiskAnalysisDto();
        result.setPortfolioId(portfolioId);
        result.setPortfolioName(portfolio.getName());
        result.setCurrency(portfolio.getBaseCurrency());
        result.setPortfolioAnnualizedReturn(round(portReturn * 100));
        result.setPortfolioVolatility(round(portVolatility * 100));
        result.setPortfolioSharpeRatio(round(sharpeRatio(portReturn, portVolatility)));
        result.setPortfolioBeta(round(calculateBeta(portfolioReturns, benchmarkReturns)));
        result.setPortfolioMaxDrawdown(round(calculatePortfolioMaxDrawdown(positions, portfolio.getBaseCurrency(), startDate, endDate, totalMarketValue) * 100));
        result.setPortfolioVar95(round(valueAtRisk95(portfolioReturns) * 100));
        result.setRiskFreeRate(BigDecimal.valueOf(RISK_FREE_RATE * 100).setScale(2, RoundingMode.HALF_UP));
        result.setDiversificationBenefit(round((weightedSumVola - portVolatility) * 100));
        result.setSecurities(securityRisks);

        return result;
    }

    private double annualizedVolatility(final List<Double> dailyReturns) {
        if (dailyReturns.size() < 2) return 0.0;
        double mean = dailyReturns.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = dailyReturns.stream().mapToDouble(r -> Math.pow(r - mean, 2)).average().orElse(0.0);
        return Math.sqrt(variance) * Math.sqrt(TRADING_DAYS_PER_YEAR);
    }

    private double annualizedReturn(final List<Double> dailyReturns) {
        if (dailyReturns.isEmpty()) return 0.0;
        double product = dailyReturns.stream().mapToDouble(r -> 1.0 + r).reduce(1.0, (a, b) -> a * b);
        return Math.pow(product, TRADING_DAYS_PER_YEAR / dailyReturns.size()) - 1.0;
    }

    private double sharpeRatio(final double annReturn, final double volatility) {
        if (volatility == 0.0) return 0.0;
        return (annReturn - RISK_FREE_RATE) / volatility;
    }

    private double calculateBeta(final List<Double> assetReturns, final List<Double> benchmarkReturns) {
        if (assetReturns.isEmpty() || benchmarkReturns.isEmpty()) return 1.0;
        int n = Math.min(assetReturns.size(), benchmarkReturns.size());
        if (n < 2) return 1.0;

        List<Double> asset = assetReturns.subList(assetReturns.size() - n, assetReturns.size());
        List<Double> bench = benchmarkReturns.subList(benchmarkReturns.size() - n, benchmarkReturns.size());

        double meanAsset = asset.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double meanBench = bench.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        double covariance = 0.0;
        double varianceBench = 0.0;
        for (int i = 0; i < n; i++) {
            covariance += (asset.get(i) - meanAsset) * (bench.get(i) - meanBench);
            varianceBench += Math.pow(bench.get(i) - meanBench, 2);
        }

        if (varianceBench == 0.0) return 1.0;
        return covariance / varianceBench;
    }

    private double maxDrawdown(final String symbol, final LocalDate start, final LocalDate end) {
        List<BigDecimal> prices = getClosingPrices(symbol, start, end);
        if (prices.size() < 2) return 0.0;

        double maxDD = 0.0;
        double peak = prices.get(0).doubleValue();
        for (BigDecimal p : prices) {
            double price = p.doubleValue();
            if (price > peak) peak = price;
            double drawdown = (peak - price) / peak;
            if (drawdown > maxDD) maxDD = drawdown;
        }
        return maxDD;
    }

    private double calculatePortfolioMaxDrawdown(final List<Position> positions, final String portfolioCurrency,
                                                  final LocalDate start, final LocalDate end,
                                                  final BigDecimal totalMarketValue) {
        List<Double> values = buildPortfolioValueSeries(positions, portfolioCurrency, start, end, totalMarketValue);
        if (values.size() < 2) return 0.0;

        double maxDD = 0.0;
        double peak = values.get(0);
        for (double val : values) {
            if (val > peak) peak = val;
            if (peak > 0) {
                double dd = (peak - val) / peak;
                if (dd > maxDD) maxDD = dd;
            }
        }
        return maxDD;
    }

    private double valueAtRisk95(final List<Double> dailyReturns) {
        if (dailyReturns.size() < 20) return 0.0;
        List<Double> sorted = dailyReturns.stream().sorted().collect(Collectors.toList());
        int index = (int) Math.floor(0.05 * sorted.size());
        return -sorted.get(Math.max(0, index));
    }

    private List<Double> getDailyReturns(final String symbol, final LocalDate start, final LocalDate end) {
        List<BigDecimal> prices = getClosingPrices(symbol, start, end);
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < prices.size(); i++) {
            double prev = prices.get(i - 1).doubleValue();
            double curr = prices.get(i).doubleValue();
            if (prev != 0.0) returns.add((curr - prev) / prev);
        }
        return returns;
    }

    private List<BigDecimal> getClosingPrices(final String symbol, final LocalDate start, final LocalDate end) {
        List<BigDecimal> prices = new ArrayList<>();
        try {
            HistoricalResponse response = yFinanceClient.getHistorical(symbol, start, end, "1d");
            if (response != null && response.getPrices() != null) {
                response.getPrices().stream()
                        .filter(p -> p.getClose() != null)
                        .sorted(Comparator.comparing(HistoricalPrice::getDate))
                        .forEach(p -> prices.add(p.getClose()));
            }
        } catch (Exception e) {
            // yFinance nicht erreichbar
        }
        return prices;
    }

    private List<Double> calculatePortfolioReturns(final List<Position> positions, final String portfolioCurrency,
                                                    final LocalDate start, final LocalDate end,
                                                    final BigDecimal totalMarketValue) {
        if (totalMarketValue.compareTo(BigDecimal.ZERO) == 0) return Collections.emptyList();

        Map<String, List<Double>> returnsBySymbol = new HashMap<>();
        Map<String, Double> weights = new HashMap<>();

        for (Position pos : positions) {
            String symbol = pos.getSecurity().getSymbol();
            List<Double> returns = getDailyReturns(symbol, start, end);
            if (!returns.isEmpty()) {
                returnsBySymbol.put(symbol, returns);
                double weight = getCurrentValue(pos, portfolioCurrency)
                        .divide(totalMarketValue, 6, RoundingMode.HALF_UP).doubleValue();
                weights.put(symbol, weight);
            }
        }

        if (returnsBySymbol.isEmpty()) return Collections.emptyList();

        int minLength = returnsBySymbol.values().stream().mapToInt(List::size).min().orElse(0);
        List<Double> portfolioReturns = new ArrayList<>();
        for (int i = 0; i < minLength; i++) {
            double portReturn = 0.0;
            for (Map.Entry<String, List<Double>> entry : returnsBySymbol.entrySet()) {
                portReturn += weights.getOrDefault(entry.getKey(), 0.0) * entry.getValue().get(i);
            }
            portfolioReturns.add(portReturn);
        }
        return portfolioReturns;
    }

    private List<Double> buildPortfolioValueSeries(final List<Position> positions, final String portfolioCurrency,
                                                    final LocalDate start, final LocalDate end,
                                                    final BigDecimal totalMarketValue) {
        List<Double> returns = calculatePortfolioReturns(positions, portfolioCurrency, start, end, totalMarketValue);
        List<Double> values = new ArrayList<>();
        double value = 100.0;
        values.add(value);
        for (double r : returns) {
            value *= (1.0 + r);
            values.add(value);
        }
        return values;
    }

    private BigDecimal getCurrentValue(final Position position, final String portfolioCurrency) {
        try {
            var quote = yFinanceClient.getQuote(position.getSecurity().getSymbol());
            if (quote == null || quote.getCurrentPrice() == null) return BigDecimal.ZERO;
            BigDecimal fxRate = getFxRate(position.getSecurity().getTradingCurrency(), portfolioCurrency);
            return quote.getCurrentPrice().multiply(fxRate).multiply(BigDecimal.valueOf(position.getTotalQuantity()));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getFxRate(final String from, final String to) {
        if (from == null || to == null || from.equalsIgnoreCase(to)) return BigDecimal.ONE;
        return fxRateRepository
                .findTopByBaseCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(from, to, LocalDate.now())
                .map(fx -> fx.getRate())
                .orElse(BigDecimal.ONE);
    }

    private BigDecimal round(final double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
