package ch.allianz.jt.service;

import ch.allianz.jt.entity.FxRate;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FxRateService {

    FxRate createFxRate(FxRate fxRate);

    List<FxRate> getAll();

    Optional<FxRate> getLatestRate(String baseCurrency, String quoteCurrency, LocalDate date);
}
