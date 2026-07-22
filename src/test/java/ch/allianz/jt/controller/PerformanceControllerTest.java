package ch.allianz.jt.controller;

import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.repository.TransactionRepository;
import ch.allianz.jt.service.PerformanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceControllerTest {

    @Mock PerformanceService performanceService;
    @Mock TransactionRepository transactionRepository;
    @Mock PositionRepository positionRepository;
    @Mock FxRateRepository fxRateRepository;
    @Mock PortfolioRepository portfolioRepository;

    @Test
    void yearlyBreakdown_sollAnPerformanceServiceDelegieren() {
        PerformanceController controller = new PerformanceController(
                performanceService, transactionRepository, positionRepository, fxRateRepository, portfolioRepository);

        Map<String, Object> expected = Map.of("years", "dummy");
        when(performanceService.getYearlyBreakdown(1L, "CHF")).thenReturn(expected);

        Map<String, Object> result = controller.yearlyBreakdown(1L, "CHF");

        assertEquals(expected, result);
    }
}
