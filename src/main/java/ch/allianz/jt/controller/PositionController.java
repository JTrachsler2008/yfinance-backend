package ch.allianz.jt.controller;

import ch.allianz.jt.entity.Position;
import ch.allianz.jt.service.PositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public Position create(@RequestBody Position position) {
        return positionService.create(position);
    }
}
