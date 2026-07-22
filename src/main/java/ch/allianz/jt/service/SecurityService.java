package ch.allianz.jt.service;

import ch.allianz.jt.entity.Security;
import java.util.List;
import java.util.Optional;

public interface SecurityService {

    Security createSecurity(Security security);

    List<Security> getAll();

    Optional<Security> getById(Long id);

    Optional<Security> getBySymbol(String symbol);

    Security lookupOrCreate(String symbol);

    List<Security> refreshAll();

    Security updateSector(Long id, String sector);

    List<java.util.Map<String, String>> search(String query);
}
