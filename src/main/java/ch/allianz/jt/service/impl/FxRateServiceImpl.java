package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.FxRate;
import ch.allianz.jt.repository.FxRateRepository;
import ch.allianz.jt.service.FxRateService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FxRateServiceImpl implements FxRateService {

    private final FxRateRepository fxRateRepository;

    public FxRateServiceImpl(final FxRateRepository fxRateRepository) {
        this.fxRateRepository = fxRateRepository;
    }

    @Override
    public FxRate createFxRate(final FxRate fxRate) {
        fxRate.setCreatedAt(LocalDateTime.now());
        return fxRateRepository.save(fxRate);
    }

    @Override
    public List<FxRate> getAll() {
        return fxRateRepository.findAll();
    }

    @Override
    public Optional<FxRate> getLatestRate(final String baseCurrency, final String quoteCurrency, final LocalDate date) {
        return fxRateRepository
                .findTopByBaseCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
                        baseCurrency, quoteCurrency, date);
    }
}
