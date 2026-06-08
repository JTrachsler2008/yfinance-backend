package ch.allianz.jt.service;

import ch.allianz.jt.dto.PortfolioPerformanceDto;

public interface PerformanceService {

    PortfolioPerformanceDto getPortfolioPerformance(Long portfolioId);
}
