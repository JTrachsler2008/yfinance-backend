package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.Security;
import ch.allianz.jt.repository.SecurityRepository;
import ch.allianz.jt.service.SecurityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final SecurityRepository securityRepository;

    public SecurityServiceImpl(final SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    @Override
    public Security createSecurity(final Security security) {
        security.setCreatedAt(LocalDateTime.now());
        security.setUpdatedAt(LocalDateTime.now());
        return securityRepository.save(security);
    }

    @Override
    public List<Security> getAll() {
        return securityRepository.findAll();
    }

    @Override
    public Optional<Security> getById(final Long id) {
        return securityRepository.findById(id);
    }

    @Override
    public Optional<Security> getBySymbol(final String symbol) {
        return securityRepository.findBySymbol(symbol);
    }
}
