package ch.allianz.jt.controller;

import ch.allianz.jt.dto.PortfolioPerformanceDto;
import ch.allianz.jt.service.PerformanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolios")
public class PerformanceController {

    private final PerformanceService performanceService;

    public PerformanceController(final PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @GetMapping("/{id}/performance")
    public PortfolioPerformanceDto getPerformance(@PathVariable Long id) {
        return performanceService.getPortfolioPerformance(id);
    }
}
