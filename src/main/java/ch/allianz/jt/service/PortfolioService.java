package ch.allianz.jt.service;

import ch.allianz.jt.entity.Portfolio;
import java.util.List;

public interface PortfolioService {

    Portfolio createPortfolio(final Long userId, final Portfolio portfolio);

    List<Portfolio> getAll();
}