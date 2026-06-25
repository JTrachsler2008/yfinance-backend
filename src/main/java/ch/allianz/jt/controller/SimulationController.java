package ch.allianz.jt.controller;

import ch.allianz.jt.dto.BacktestDto;
import ch.allianz.jt.dto.SimulationDto;
import ch.allianz.jt.service.SimulationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/portfolios")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping("/{id}/simulate")
    public SimulationDto simulate(@PathVariable Long id,
                                  @RequestParam String symbol,
                                  @RequestParam Double quantity) {
        return simulationService.simulate(id, symbol, quantity);
    }

    @GetMapping("/{id}/backtest")
    public BacktestDto backtest(@PathVariable Long id,
                                @RequestParam String symbol,
                                @RequestParam Double quantity,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate buyDate) {
        return simulationService.backtest(id, symbol, quantity, buyDate);
    }
}
