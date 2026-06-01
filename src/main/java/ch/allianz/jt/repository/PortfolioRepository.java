package ch.allianz.jt.repository;

import ch.allianz.jt.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;


    public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {}

