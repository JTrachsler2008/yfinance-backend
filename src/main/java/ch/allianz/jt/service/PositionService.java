package ch.allianz.jt.service;

import ch.allianz.jt.entity.Position;
import java.util.List;

public interface PositionService {

    List<Position> getAll();

    Position create(Position position);

    List<Position> getByAccountId(Long accountId);
}
