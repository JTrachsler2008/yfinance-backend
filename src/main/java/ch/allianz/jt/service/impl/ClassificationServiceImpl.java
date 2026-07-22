package ch.allianz.jt.service.impl;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.dto.ClassificationDto;
import ch.allianz.jt.dto.ClassificationDto.ClassificationItem;
import ch.allianz.jt.entity.Position;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.service.ClassificationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class ClassificationServiceImpl implements ClassificationService {

    private final PortfolioRepository portfolioRepository;
    private final PositionRepository positionRepository;
    private final FxRateRepository fxRateRepository;
    private final YFinanceClient yFinanceClient;

    public ClassificationServiceImpl(PortfolioRepository portfolioRepository,
                                     PositionRepository positionRepository,
                                     FxRateRepository fxRateRepository,
                                     YFinanceClient yFinanceClient) {
        this.portfolioRepository = portfolioRepository;
        this.positionRepository = positionRepository;
        this.fxRateRepository = fxRateRepository;
        this.yFinanceClient = yFinanceClient;
    }

    @Override
    public ClassificationDto getClassification(Long portfolioId) {
        var portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));

        List<Position> positions = positionRepository.findByAccountPortfolioId(portfolioId);
        String baseCurrency = portfolio.getBaseCurrency();

        Map<String, BigDecimal> sectorMap = new LinkedHashMap<>();
        Map<String, BigDecimal> countryMap = new LinkedHashMap<>();
        Map<String, BigDecimal> currencyMap = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Position pos : positions) {
            BigDecimal value = getCurrentValue(pos, baseCurrency);
            total = total.add(value);

            List<String> sectors = splitSectors(pos.getSecurity().getSector());
            BigDecimal sectorShare = value.divide(BigDecimal.valueOf(sectors.size()), 6, RoundingMode.HALF_UP);
            for (String sector : sectors) {
                sectorMap.merge(sector, sectorShare, BigDecimal::add);
            }

            String country = pos.getSecurity().getCountryCode();
            if (country == null || country.isBlank()) country = "Unbekannt";
            countryMap.merge(country, value, BigDecimal::add);

            String currency = pos.getSecurity().getTradingCurrency();
            if (currency == null || currency.isBlank()) currency = baseCurrency;
            currencyMap.merge(currency, value, BigDecimal::add);
        }

        ClassificationDto result = new ClassificationDto();
        result.setPortfolioId(portfolioId);
        result.setTotalValue(total);
        result.setBySector(toItems(sectorMap, total));
        result.setByCountry(toItems(countryMap, total));
        result.setByCurrency(toItems(currencyMap, total));
        return result;
    }

    private List<String> splitSectors(String raw) {
        if (raw == null || raw.isBlank()) return List.of("Unbekannt");
        List<String> sectors = new ArrayList<>();
        for (String s : raw.split(",")) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) sectors.add(trimmed);
        }
        return sectors.isEmpty() ? List.of("Unbekannt") : sectors;
    }

    private List<ClassificationItem> toItems(Map<String, BigDecimal> map, BigDecimal total) {
        List<ClassificationItem> items = new ArrayList<>();
        for (var entry : map.entrySet()) {
            BigDecimal pct = total.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : entry.getValue().divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            items.add(new ClassificationItem(entry.getKey(), entry.getValue().setScale(2, RoundingMode.HALF_UP), pct));
        }
        items.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return items;
    }

    private BigDecimal getCurrentValue(Position position, String portfolioCurrency) {
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
