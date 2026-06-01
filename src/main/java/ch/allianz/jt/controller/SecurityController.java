package ch.allianz.jt.controller;

import ch.allianz.jt.entity.Security;
import ch.allianz.jt.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/securities")
public class SecurityController {

    private final SecurityService securityService;

    public SecurityController(final SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping
    public Security create(@RequestBody Security security) {
        return securityService.createSecurity(security);
    }

    @GetMapping
    public List<Security> getAll() {
        return securityService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Security> getById(@PathVariable Long id) {
        return securityService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/symbol/{symbol}")
    public ResponseEntity<Security> getBySymbol(@PathVariable String symbol) {
        return securityService.getBySymbol(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
