package ch.allianz.jt.service;

import ch.allianz.jt.dto.BacktestDto;
import ch.allianz.jt.dto.SimulationDto;

import java.time.LocalDate;

public interface SimulationService {
    SimulationDto simulate(Long portfolioId, String symbol, Double quantity);
    BacktestDto backtest(Long portfolioId, String symbol, Double quantity, LocalDate buyDate);
}
