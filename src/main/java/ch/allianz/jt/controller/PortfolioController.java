package ch.allianz.jt.controller;

import ch.allianz.jt.service.PortfolioService;
import ch.allianz.jt.entity.Portfolio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("/{userId}")
    public Portfolio create(@PathVariable Long userId,
                            @RequestBody Portfolio portfolio) {
        return portfolioService.createPortfolio(userId, portfolio);
    }

    @GetMapping
    public List<Portfolio> getAll() {
        return portfolioService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getById(@PathVariable Long id) {
        return portfolioService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}