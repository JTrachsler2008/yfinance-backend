package ch.allianz.jt.client;

import ch.allianz.jt.generated.model.EarningsResponse;
import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.generated.model.InfoResponse;
import ch.allianz.jt.generated.model.NewsResponse;
import ch.allianz.jt.generated.model.QuoteResponse;
import ch.allianz.jt.generated.model.SnapshotResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Component
public class YFinanceClient {

    private static final String BASE_URL = "http://localhost:8000";

    private final RestTemplate restTemplate = new RestTemplate();
    private final AlphaVantageClient alphaVantageClient;

    public YFinanceClient(final AlphaVantageClient alphaVantageClient) {
        this.alphaVantageClient = alphaVantageClient;
    }

    public QuoteResponse getQuote(final String symbol) {
        try {
            return restTemplate.getForObject(BASE_URL + "/quote/" + symbol, QuoteResponse.class);
        } catch (Exception e) {
            return alphaVantageClient.getQuote(symbol);
        }
    }

    public HistoricalResponse getHistorical(final String symbol, final LocalDate start,
                                            final LocalDate end, final String interval) {
        try {
            String url = BASE_URL + "/historical/" + symbol + "?interval=" + interval;
            if (start != null) {
                url += "&start=" + start;
            }
            if (end != null) {
                url += "&end=" + end;
            }
            return restTemplate.getForObject(url, HistoricalResponse.class);
        } catch (Exception e) {
            return alphaVantageClient.getHistorical(symbol, start, end);
        }
    }

    public InfoResponse getInfo(final String symbol) {
        return restTemplate.getForObject(BASE_URL + "/info/" + symbol, InfoResponse.class);
    }

    public SnapshotResponse getSnapshot(final String symbol) {
        return restTemplate.getForObject(BASE_URL + "/snapshot/" + symbol, SnapshotResponse.class);
    }

    public NewsResponse getNews(final String symbol, final Integer count, final String tab) {
        String url = BASE_URL + "/news/" + symbol + "?count=" + count + "&tab=" + tab;
        return restTemplate.getForObject(url, NewsResponse.class);
    }

    public EarningsResponse getEarnings(final String symbol, final String frequency) {
        String url = BASE_URL + "/earnings/" + symbol + "?frequency=" + frequency;
        return restTemplate.getForObject(url, EarningsResponse.class);
    }
}