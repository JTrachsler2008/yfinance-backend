package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.api.QuoteApi;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.generated.model.ResponseGetquotesbulkValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
public class QuoteController implements QuoteApi {

    private final YFinanceClient client;

    public QuoteController(YFinanceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<QuoteResponse> getQuoteBySymbol(String symbol, String xAPIKey) {

        QuoteResponse response = client.getQuote(symbol);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, ResponseGetquotesbulkValue>> getQuotesBulk(String symbols, String xAPIKey) {
        Map<String, ResponseGetquotesbulkValue> result = new HashMap<>();

        Arrays.stream(symbols.split(","))
                .map(String::trim)
                .forEach(symbol -> {
                    QuoteResponse quote = client.getQuote(symbol);
                    if (quote != null) {
                        ResponseGetquotesbulkValue value = new ResponseGetquotesbulkValue();
                        value.setSymbol(quote.getSymbol());
                        value.setCurrentPrice(quote.getCurrentPrice());
                        value.setPreviousClose(quote.getPreviousClose());
                        value.setOpenPrice(quote.getOpenPrice());
                        value.setHigh(quote.getHigh());
                        value.setLow(quote.getLow());
                        value.setVolume(quote.getVolume());
                        result.put(symbol, value);
                    }
                });

        return ResponseEntity.ok(result);
    }
}