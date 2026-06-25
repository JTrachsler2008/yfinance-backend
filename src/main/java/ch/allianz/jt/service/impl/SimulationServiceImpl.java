package ch.allianz.jt.service.impl;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.BacktestDto;
import ch.allianz.jt.dto.BacktestDto.ChartPoint;
import ch.allianz.jt.dto.SimulationDto;
import ch.allianz.jt.dto.SimulationDto.WeightItem;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.service.SecurityService;
import ch.allianz.jt.service.SimulationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class SimulationServiceImpl implements SimulationService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final FxRateRepository fxRateRepository;
    private final YFinanceClient yFinanceClient;
    private final SecurityService securityService;

    public SimulationServiceImpl(PortfolioRepository portfolioRepository,
                                 PositionRepository positionRepository,
                                 FxRateRepository fxRateRepository,
                                 YFinanceClient yFinanceClient,
                                 SecurityService securityService) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.fxRateRepository = fxRateRepository;
        this.yFinanceClient = yFinanceClient;
        this.securityService = securityService;
    }

    @Override
    public SimulationDto simulate(Long portfolioId, String symbol, Double quantity) {
        var portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        String baseCurrency = portfolio.getBaseCurrency();

        var security = securityService.lookupOrCreate(symbol);
        QuoteResponse quote = yFinanceClient.getQuote(symbol.toUpperCase());
        if (quote == null || quote.getCurrentPrice() == null)
            throw new RuntimeException("Kurs nicht verfügbar für: " + symbol);

        BigDecimal currentPrice = quote.getCurrentPrice();
        BigDecimal cost = currentPrice.multiply(BigDecimal.valueOf(quantity));

        List<Position> positions = positionRepository.findByAccountPortfolioId(portfolioId);
        BigDecimal currentTotal = BigDecimal.ZERO;
        List<WeightItem> currentWeights = new ArrayList<>();

        for (Position pos : positions) {
            BigDecimal val = getPositionValue(pos, baseCurrency);
            currentTotal = currentTotal.add(val);
            currentWeights.add(new WeightItem(pos.getSecurity().getSymbol(), val, BigDecimal.ZERO));
        }

        BigDecimal simulatedTotal = currentTotal.add(cost);
        List<WeightItem> simulatedWeights = new ArrayList<>();

        for (WeightItem w : currentWeights) {
            BigDecimal pct = simulatedTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : w.getValue().divide(simulatedTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            simulatedWeights.add(new WeightItem(w.getSymbol(), w.getValue(), pct));
        }

        BigDecimal newPct = simulatedTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : cost.divide(simulatedTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        simulatedWeights.add(new WeightItem(symbol.toUpperCase(), cost, newPct));

        BigDecimal finalTotal = currentTotal;
        currentWeights.forEach(w -> {
            BigDecimal pct = finalTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : w.getValue().divide(finalTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            currentWeights.set(currentWeights.indexOf(w), new WeightItem(w.getSymbol(), w.getValue(), pct));
        });

        SimulationDto result = new SimulationDto();
        result.setSymbol(symbol.toUpperCase());
        result.setSecurityName(security.getName());
        result.setCurrentPrice(currentPrice.setScale(2, RoundingMode.HALF_UP));
        result.setQuantity(quantity);
        result.setCost(cost.setScale(2, RoundingMode.HALF_UP));
        result.setCurrentPortfolioValue(currentTotal.setScale(2, RoundingMode.HALF_UP));
        result.setSimulatedPortfolioValue(simulatedTotal.setScale(2, RoundingMode.HALF_UP));
        result.setValueChange(cost.setScale(2, RoundingMode.HALF_UP));
        result.setReturnChangePercent(currentTotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : cost.divide(currentTotal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP));
        result.setCurrentWeights(currentWeights);
        result.setSimulatedWeights(simulatedWeights);
        return result;
    }

    @Override
    public BacktestDto backtest(Long portfolioId, String symbol, Double quantity, LocalDate buyDate) {
        var security = securityService.lookupOrCreate(symbol);
        QuoteResponse quote = yFinanceClient.getQuote(symbol.toUpperCase());
        BigDecimal currentPrice = (quote != null && quote.getCurrentPrice() != null)
                ? quote.getCurrentPrice() : BigDecimal.ZERO;

        LocalDate endDate = LocalDate.now().minusDays(1);
        HistoricalResponse historical = yFinanceClient.getHistorical(symbol.toUpperCase(), buyDate, endDate, "1d");

        BigDecimal priceAtBuy = BigDecimal.ZERO;
        List<ChartPoint> chartData = new ArrayList<>();

        if (historical != null && historical.getPrices() != null) {
            List<HistoricalPrice> prices = historical.getPrices().stream()
                    .filter(p -> p.getClose() != null && p.getDate() != null)
                    .sorted(Comparator.comparing(HistoricalPrice::getDate))
                    .toList();

            if (!prices.isEmpty()) {
                priceAtBuy = prices.get(0).getClose();
                for (HistoricalPrice p : prices) {
                    BigDecimal portfolioVal = p.getClose().multiply(BigDecimal.valueOf(quantity));
                    chartData.add(new ChartPoint(p.getDate().toString(), p.getClose(), portfolioVal));
                }
            }
        }

        BigDecimal invested = priceAtBuy.multiply(BigDecimal.valueOf(quantity));
        BigDecimal valueNow = currentPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal gainLoss = valueNow.subtract(invested);
        BigDecimal returnPct = invested.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : gainLoss.divide(invested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        BacktestDto result = new BacktestDto();
        result.setSymbol(symbol.toUpperCase());
        result.setSecurityName(security.getName());
        result.setBuyDate(buyDate);
        result.setQuantity(quantity);
        result.setPriceAtBuy(priceAtBuy.setScale(2, RoundingMode.HALF_UP));
        result.setCurrentPrice(currentPrice.setScale(2, RoundingMode.HALF_UP));
        result.setInvestedAmount(invested.setScale(2, RoundingMode.HALF_UP));
        result.setCurrentValue(valueNow.setScale(2, RoundingMode.HALF_UP));
        result.setGainLoss(gainLoss.setScale(2, RoundingMode.HALF_UP));
        result.setReturnPercent(returnPct.setScale(2, RoundingMode.HALF_UP));
        result.setPriceHistory(chartData);
        return result;
    }

    private BigDecimal getPositionValue(Position position, String portfolioCurrency) {
        try {
            var quote = yFinanceClient.getQuote(position.getSecurity().getSymbol());
            if (quote == null || quote.getCurrentPrice() == null) return BigDecimal.ZERO;
            BigDecimal fxRate = fxRateRepository
                    .findTopByBaseCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
                            position.getSecurity().getTradingCurrency(), portfolioCurrency, LocalDate.now())
                    .map(fx -> fx.getRate()).orElse(BigDecimal.ONE);
            return quote.getCurrentPrice().multiply(fxRate).multiply(BigDecimal.valueOf(position.getTotalQuantity()));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
