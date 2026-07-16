package ch.allianz.jt.controller;

import ch.allianz.jt.entity.Position;
import ch.allianz.jt.service.PositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(final PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public List<Position> getAll() {
        return positionService.getAll();
    }

    @GetMapping("/account/{accountId}")
    public List<Position> getByAccount(@PathVariable Long accountId) {
        return positionService.getByAccountId(accountId);
    }

    @GetMapping("/account/{accountId}/security/{securityId}/lots")
    public List<Map<String, Object>> getLotsFifo(@PathVariable Long accountId, @PathVariable Long securityId) {
        return positionService.getLotsFifo(accountId, securityId);
    }
}
