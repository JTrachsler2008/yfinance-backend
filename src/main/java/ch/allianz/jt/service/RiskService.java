package ch.allianz.jt.service;

import ch.allianz.jt.dto.RiskAnalysisDto;

public interface RiskService {

    RiskAnalysisDto getRiskAnalysis(Long portfolioId, int lookbackDays, String from, String to);
}
