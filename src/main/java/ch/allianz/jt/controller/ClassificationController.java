package ch.allianz.jt.controller;

import ch.allianz.jt.dto.ClassificationDto;
import ch.allianz.jt.service.ClassificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portfolios")
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @GetMapping("/{id}/classification")
    public ClassificationDto getClassification(@PathVariable Long id) {
        return classificationService.getClassification(id);
    }
}
