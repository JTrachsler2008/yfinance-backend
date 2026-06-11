package ch.allianz.jt.service.impl;

import ch.allianz.jt.entity.Position;
import ch.allianz.jt.repository.PositionRepository;
import ch.allianz.jt.service.PositionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    public PositionServiceImpl(final PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    @Override
    public List<Position> getAll() {
        return positionRepository.findAll();
    }

    @Override
    public Position create(final Position position) {
        return positionRepository.save(position);
    }

    @Override
    public List<Position> getByAccountId(final Long accountId) {
        return positionRepository.findByAccountId(accountId);
    }
}
