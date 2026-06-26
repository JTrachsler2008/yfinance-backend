package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.Portfolio;
import ch.allianz.jt.entity.User;
import ch.allianz.jt.exception.ResourceNotFoundException;
import ch.allianz.jt.repository.PortfolioRepository;
import ch.allianz.jt.repository.UserRepository;
import ch.allianz.jt.service.PortfolioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public PortfolioServiceImpl(final PortfolioRepository portfolioRepository,
                                final UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Portfolio createPortfolio(final Long userId, final Portfolio portfolio) {

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        portfolio.setUser(user);
        return portfolioRepository.save(portfolio);
    }

    @Override
    public List<Portfolio> getAll() {
        return portfolioRepository.findAll();
    }

    @Override
    public Optional<Portfolio> getById(final Long id) {
        return portfolioRepository.findById(id);
    }

    @Override
    public Portfolio updateCurrency(final Long id, final String currency) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + id));
        portfolio.setBaseCurrency(currency);
        return portfolioRepository.save(portfolio);
    }
}