package ch.allianz.jt.service;

import ch.allianz.jt.entity.Position;
import java.util.List;
import java.util.Map;

public interface PositionService {

    List<Position> getAll();

    Position create(Position position);

    List<Position> getByAccountId(Long accountId);

    List<Map<String, Object>> getLotsFifo(Long accountId, Long securityId);
}
