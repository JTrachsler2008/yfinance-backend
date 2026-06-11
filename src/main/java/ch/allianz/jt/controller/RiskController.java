package ch.allianz.jt.controller;

import ch.allianz.jt.dto.RiskAnalysisDto;
import ch.allianz.jt.service.RiskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolios")
public class RiskController {

    private final RiskService riskService;

    public RiskController(final RiskService riskService) {
        this.riskService = riskService;
    }

    /**
     * Vollständige Risikoanalyse eines Portfolios.
     * Enthält: Volatilität, Sharpe Ratio, Beta, Max Drawdown, VaR 95%
     * sowie Daten für das Rendite/Risiko-Streudiagramm (pro Security).
     */
    @GetMapping("/{id}/risk")
    public RiskAnalysisDto getRiskAnalysis(@PathVariable Long id) {
        return riskService.getRiskAnalysis(id);
    }
}
