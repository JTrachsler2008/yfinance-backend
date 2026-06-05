package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.api.InfoApi;
import ch.allianz.jt.generated.model.InfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController implements InfoApi {

    private final YFinanceClient client;

    public InfoController(final YFinanceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<InfoResponse> getInfoBySymbol(final String symbol, final String xAPIKey) {
        InfoResponse response = client.getInfo(symbol);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
