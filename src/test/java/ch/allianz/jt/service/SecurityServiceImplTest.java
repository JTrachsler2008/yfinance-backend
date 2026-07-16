package ch.allianz.jt.service;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.entity.Security;
import ch.allianz.jt.generated.model.InfoResponse;
import ch.allianz.jt.repository.SecurityRepository;
import ch.allianz.jt.service.impl.SecurityServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @Mock SecurityRepository securityRepository;
    @Mock YFinanceClient yFinanceClient;

    SecurityServiceImpl securityService;

    @Test
    void createSecurity_sollZeitstempelSetzenUndSpeichern() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        Security security = new Security();
        security.setSymbol("AAPL");
        when(securityRepository.save(security)).thenReturn(security);

        Security result = securityService.createSecurity(security);

        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void getAll_sollAlleSecuritiesZurueckgeben() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        Security s1 = new Security();
        when(securityRepository.findAll()).thenReturn(List.of(s1));

        List<Security> result = securityService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getById_wennNichtGefunden_sollEmptyZurueckgeben() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        when(securityRepository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(securityService.getById(99L).isEmpty());
    }

    @Test
    void getBySymbol_sollAnRepositoryDelegieren() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        Security s = new Security();
        s.setSymbol("MSFT");
        when(securityRepository.findBySymbol("MSFT")).thenReturn(Optional.of(s));

        Optional<Security> result = securityService.getBySymbol("MSFT");

        assertTrue(result.isPresent());
        assertEquals("MSFT", result.get().getSymbol());
    }

    @Test
    void lookupOrCreate_wennBereitsVorhanden_sollBestehendeZurueckgeben() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        Security existing = new Security();
        existing.setSymbol("AAPL");
        when(securityRepository.findBySymbol("AAPL")).thenReturn(Optional.of(existing));

        Security result = securityService.lookupOrCreate("aapl");

        assertSame(existing, result);
    }

    @Test
    void lookupOrCreate_wennNichtVorhanden_sollNeuAnlegenMitInfoDaten() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        when(securityRepository.findBySymbol("NVDA")).thenReturn(Optional.empty());

        InfoResponse info = new InfoResponse();
        info.setLongName("NVIDIA Corporation");
        info.setCurrency("USD");
        info.setCountry("US");
        info.setSector("Technology");
        when(yFinanceClient.getInfo("NVDA")).thenReturn(info);
        when(securityRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(inv -> inv.getArgument(0));

        Security result = securityService.lookupOrCreate("nvda");

        assertEquals("NVDA", result.getSymbol());
        assertEquals("NVIDIA Corporation", result.getName());
        assertEquals("USD", result.getTradingCurrency());
        assertEquals("STOCK", result.getAssetType());
    }

    @Test
    void lookupOrCreate_wennInfoNichtVerfuegbar_sollFallbackVerwenden() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        when(securityRepository.findBySymbol("XYZ")).thenReturn(Optional.empty());
        when(yFinanceClient.getInfo("XYZ")).thenThrow(new RuntimeException("nicht erreichbar"));
        when(securityRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(inv -> inv.getArgument(0));

        Security result = securityService.lookupOrCreate("xyz");

        assertEquals("XYZ", result.getSymbol());
        assertEquals("XYZ", result.getName());
        assertEquals("USD", result.getTradingCurrency());
    }

    @Test
    void refreshAll_sollFehlendeFelderAusInfoErgaenzen() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        Security sec = new Security();
        sec.setSymbol("AAPL");
        when(securityRepository.findAll()).thenReturn(List.of(sec));

        InfoResponse info = new InfoResponse();
        info.setSector("Technology");
        info.setCountry("US");
        when(yFinanceClient.getInfo("AAPL")).thenReturn(info);

        securityService.refreshAll();

        assertEquals("Technology", sec.getSector());
        assertEquals("US", sec.getCountryCode());
    }

    @Test
    void refreshAll_wennInfoFehlschlaegt_sollWeitermachen() {
        securityService = new SecurityServiceImpl(securityRepository, yFinanceClient);
        Security sec = new Security();
        sec.setSymbol("BROKEN");
        when(securityRepository.findAll()).thenReturn(List.of(sec));
        when(yFinanceClient.getInfo("BROKEN")).thenThrow(new RuntimeException("down"));

        List<Security> result = securityService.refreshAll();

        assertNotNull(result);
    }
}
