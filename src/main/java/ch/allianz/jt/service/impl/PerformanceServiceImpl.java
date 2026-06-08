package ch.allianz.jt.service.impl;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.PortfolioPerformanceDto;
import ch.allianz.jt.dto.PositionPerformanceDto;
import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.service.PerformanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final YFinanceClient yFinanceClient;

    public PerformanceServiceImpl(final PortfolioRepository portfolioRepository,
                                   final PositionRepository positionRepository,
                                   final YFinanceClient yFinanceClient) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.yFinanceClient = yFinanceClient;
    }

    @Override
    public PortfolioPerformanceDto getPortfolioPerformance(final Long portfolioId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<Position> positions = positionRepository.findByAccountPortfolioId(portfolioId);

        BigDecimal totalMarketValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        List<PositionPerformanceDto> positionDtos = positions.stream().map(position -> {
            PositionPerformanceDto dto = new PositionPerformanceDto();
            dto.setSymbol(position.getSecurity().getSymbol());
            dto.setSecurityName(position.getSecurity().getName());
            dto.setQuantity(position.getTotalQuantity());
            dto.setAveragePurchasePrice(position.getAveragePurchasePrice());

            // Aktuellen Kurs holen
            QuoteResponse quote = yFinanceClient.getQuote(position.getSecurity().getSymbol());
            BigDecimal currentPrice = quote != null ? quote.getCurrentPrice() : BigDecimal.ZERO;
            dto.setCurrentPrice(currentPrice);

            // Marktwert = Anzahl × aktueller Kurs
            BigDecimal quantity = BigDecimal.valueOf(position.getTotalQuantity());
            BigDecimal marketValue = currentPrice.multiply(quantity);
            dto.setMarketValue(marketValue);

            // Gewinn/Verlust = Marktwert - (Anzahl × Einstandspreis)
            BigDecimal cost = position.getAveragePurchasePrice().multiply(quantity);
            BigDecimal gainLoss = marketValue.subtract(cost);
            dto.setGainLoss(gainLoss);

            // Gewinn/Verlust in % = (Gewinn / Kosten) × 100
            if (cost.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal gainLossPercent = gainLoss
                        .divide(cost, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                dto.setGainLossPercent(gainLossPercent);
            } else {
                dto.setGainLossPercent(BigDecimal.ZERO);
            }

            return dto;
        }).toList();

        // Gesamtwerte berechnen
        for (PositionPerformanceDto pos : positionDtos) {
            totalMarketValue = totalMarketValue.add(pos.getMarketValue());
            totalCost = totalCost.add(
                    pos.getAveragePurchasePrice()
                       .multiply(BigDecimal.valueOf(pos.getQuantity()))
            );
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
}
