package ch.allianz.jt.client;

import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.generated.model.QuoteResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Component
public class YFinanceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public QuoteResponse getQuote(String symbol) {
        String url = "http://localhost:8000/quote/" + symbol;
        return restTemplate.getForObject(url, QuoteResponse.class);
    }

    public HistoricalResponse getHistorical(String symbol, LocalDate start, LocalDate end, String interval) {

        String url = "http://localhost:8000/historical/" + symbol +
                "?interval=" + interval;

        if (start != null) {
            url += "&start=" + start;
        }

        if (end != null) {
            url += "&end=" + end;
        }

        return restTemplate.getForObject(url, HistoricalResponse.class);
    }
}