package ch.allianz.jt.service;

import ch.allianz.jt.dto.PortfolioPerformanceDto;
import java.util.List;
import java.util.Map;

public interface PerformanceService {

    PortfolioPerformanceDto getPortfolioPerformance(Long portfolioId);

    List<Map<String, Object>> getPortfolioHistory(Long portfolioId, int months);
}
