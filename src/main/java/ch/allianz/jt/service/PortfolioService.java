package ch.allianz.jt.service;

import ch.allianz.jt.entity.Portfolio;
import java.util.List;
import java.util.Optional;

public interface PortfolioService {

    Portfolio createPortfolio(final Long userId, final Portfolio portfolio);

    List<Portfolio> getAll();

    Optional<Portfolio> getById(Long id);

    Portfolio updateCurrency(Long id, String currency);

    void deletePortfolio(Long id);
}