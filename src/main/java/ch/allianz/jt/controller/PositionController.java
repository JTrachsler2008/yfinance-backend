package ch.allianz.jt.controller;

import ch.allianz.jt.entity.Position;
import ch.allianz.jt.repository.PositionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionRepository positionRepository;

    public PositionController(final PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @GetMapping
    public List<Position> getAll() {
        return positionRepository.findAll();
    }

    @PostMapping
    public Position create(@RequestBody Position position) {
        return positionRepository.save(position);
    }
}
