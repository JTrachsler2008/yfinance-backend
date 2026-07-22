package ch.allianz.jt.service;

import ch.allianz.jt.dto.PortfolioPerformanceDto;
import java.util.List;
import java.util.Map;

public interface PerformanceService {

    PortfolioPerformanceDto getPortfolioPerformance(Long portfolioId, String currency);

    List<Map<String, Object>> getPortfolioHistory(Long portfolioId, int months, String currency, String from, String to, String granularity);

    Map<String, Object> getYearlyBreakdown(Long portfolioId, String currency);
}
