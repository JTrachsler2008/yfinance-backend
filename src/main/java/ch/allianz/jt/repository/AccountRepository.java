package ch.allianz.jt.repository;


import ch.allianz.jt.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a JOIN FETCH a.portfolio WHERE a.id = :id")
    java.util.Optional<Account> findByIdWithPortfolio(Long id);
}

