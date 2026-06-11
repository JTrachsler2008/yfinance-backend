package ch.allianz.jt.service;

import ch.allianz.jt.dto.RiskAnalysisDto;

public interface RiskService {

    RiskAnalysisDto getRiskAnalysis(Long portfolioId);
}
