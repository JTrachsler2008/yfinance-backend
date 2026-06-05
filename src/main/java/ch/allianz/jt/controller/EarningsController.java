package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.api.EarningsApi;
import ch.allianz.jt.generated.model.EarningsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EarningsController implements EarningsApi {

    private final YFinanceClient client;

    public EarningsController(final YFinanceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<EarningsResponse> getEarningsBySymbol(final String symbol,
                                                                 final String frequency,
                                                                 final String xAPIKey) {
        EarningsResponse response = client.getEarnings(symbol, frequency);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
