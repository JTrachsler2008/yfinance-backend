package ch.allianz.jt.service.impl;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.PortfolioPerformanceDto;
import ch.allianz.jt.dto.PositionPerformanceDto;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.service.PerformanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final FxRateRepository fxRateRepository;
    private final YFinanceClient yFinanceClient;

    public PerformanceServiceImpl(final PortfolioRepository portfolioRepository,
                                   final PositionRepository positionRepository,
                                   final FxRateRepository fxRateRepository,
                                   final YFinanceClient yFinanceClient) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
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
                dto.setGainLossPercent(gainLoss.divide(cost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)));
            } else {
                dto.setGainLossPercent(BigDecimal.ZERO);
            }

            return dto;
        }).toList();

        for (PositionPerformanceDto pos : positionDtos) {
            totalMarketValue = totalMarketValue.add(pos.getMarketValue());
            totalCost = totalCost.add(
                    pos.getAveragePurchasePrice().multiply(BigDecimal.valueOf(pos.getQuantity())));
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
        result.setPositions(positionDtos);

        return result;
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
