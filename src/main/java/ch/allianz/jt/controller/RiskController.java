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

    @GetMapping("/{id}/risk")
    public RiskAnalysisDto getRiskAnalysis(@PathVariable Long id,
                                            @RequestParam(defaultValue = "365") int lookbackDays,
                                            @RequestParam(required = false) String from,
                                            @RequestParam(required = false) String to) {
        return riskService.getRiskAnalysis(id, lookbackDays, from, to);
    }
}
