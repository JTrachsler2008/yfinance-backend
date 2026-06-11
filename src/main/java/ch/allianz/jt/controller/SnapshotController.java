package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.api.SnapshotApi;
import ch.allianz.jt.generated.model.SnapshotResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SnapshotController implements SnapshotApi {

    private final YFinanceClient client;

    public SnapshotController(final YFinanceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<SnapshotResponse> getSnapshotBySymbol(final String symbol, final String xAPIKey) {
        SnapshotResponse response = client.getSnapshot(symbol);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}