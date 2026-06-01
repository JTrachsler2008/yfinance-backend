package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.api.HistoricalApi;
import ch.allianz.jt.generated.model.HistoricalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class HistoricalController implements HistoricalApi {

    private final YFinanceClient client;

    public HistoricalController(YFinanceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<HistoricalResponse> getHistoricalDataBySymbol(
            String symbol,
            LocalDate start,
            LocalDate end,
            String interval,
            String xAPIKey) {

        HistoricalResponse response = client.getHistorical(symbol, start, end, interval);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}