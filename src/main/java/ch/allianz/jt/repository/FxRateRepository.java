package ch.allianz.jt.repository;

import ch.allianz.jt.entity.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface FxRateRepository extends JpaRepository<FxRate, Long> {

    Optional<FxRate> findTopByBaseCurrencyAndQuoteCurrencyAndRateDateLessThanEqualOrderByRateDateDesc(
            String baseCurrency, String quoteCurrency, java.time.LocalDate date);
}

