package ch.allianz.jt.controller;

import ch.allianz.jt.entity.FxRate;
import ch.allianz.jt.service.FxRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fx-rates")
public class FxRateController {

    private final FxRateService fxRateService;

    public FxRateController(final FxRateService fxRateService) {
        this.fxRateService = fxRateService;
    }

    @PostMapping
    public FxRate create(@RequestBody FxRate fxRate) {
        return fxRateService.createFxRate(fxRate);
    }

    @GetMapping
    public List<FxRate> getAll() {
        return fxRateService.getAll();
    }

    @GetMapping("/latest")
    public ResponseEntity<FxRate> getLatest(
            @RequestParam String baseCurrency,
            @RequestParam String quoteCurrency,
            @RequestParam(required = false) LocalDate date) {

        LocalDate effectiveDate = date != null ? date : LocalDate.now();

        return fxRateService.getLatestRate(baseCurrency, quoteCurrency, effectiveDate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
