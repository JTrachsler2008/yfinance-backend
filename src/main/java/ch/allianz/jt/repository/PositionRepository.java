package ch.allianz.jt.repository;

import ch.allianz.jt.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {

    List<Position> findByAccountPortfolioId(Long portfolioId);

    Optional<Position> findByAccountIdAndSecurityId(Long accountId, Long securityId);
}
