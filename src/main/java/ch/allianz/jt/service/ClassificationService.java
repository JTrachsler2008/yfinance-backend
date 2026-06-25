package ch.allianz.jt.service;

import ch.allianz.jt.dto.ClassificationDto;

public interface ClassificationService {
    ClassificationDto getClassification(Long portfolioId);
}
