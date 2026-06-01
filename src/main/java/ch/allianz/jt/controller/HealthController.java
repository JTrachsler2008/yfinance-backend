package ch.allianz.jt.controller;
import java.util.Map;

import ch.allianz.jt.generated.api.HealthApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController implements HealthApi {

    @Override
    public ResponseEntity<Object> getHealthStatus() {
        return ResponseEntity.ok(Map.of("status", "OK"));
    }
}