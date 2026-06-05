package ch.allianz.jt.client;

import ch.allianz.jt.generated.model.HistoricalPrice;
import ch.allianz.jt.generated.model.HistoricalResponse;
import ch.allianz.jt.generated.model.QuoteResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

@Component
public class AlphaVantageClient {

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.api.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuoteResponse getQuote(final String symbol) {
        String url = baseUrl + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apiKey;

        try {
            String json = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(json);
            JsonNode quote = root.path("Global Quote");

            if (quote.isMissingNode() || quote.isEmpty()) {
                return null;
            }

            QuoteResponse response = new QuoteResponse();
            response.setSymbol(symbol);
            response.setCurrentPrice(new BigDecimal(quote.path("05. price").asText()));
            response.setPreviousClose(new BigDecimal(quote.path("08. previous close").asText()));
            response.setOpenPrice(new BigDecimal(quote.path("02. open").asText()));
            response.setHigh(new BigDecimal(quote.path("03. high").asText()));
            response.setLow(new BigDecimal(quote.path("04. low").asText()));
            response.setVolume(quote.path("06. volume").asInt());
            return response;

        } catch (Exception e) {
            return null;
        }
    }

    public HistoricalResponse getHistorical(final String symbol, final LocalDate start, final LocalDate end) {
        String url = baseUrl + "?function=TIME_SERIES_DAILY&symbol=" + symbol
                + "&outputsize=full&apikey=" + apiKey;

        try {
            String json = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(json);
            JsonNode timeSeries = root.path("Time Series (Daily)");

            if (timeSeries.isMissingNode()) {
                return null;
            }

            List<HistoricalPrice> prices = new ArrayList<>();
            Iterator<Entry<String, JsonNode>> fields = timeSeries.fields();

            while (fields.hasNext()) {
                Entry<String, JsonNode> entry = fields.next();
                LocalDate date = LocalDate.parse(entry.getKey());

                if (start != null && date.isBefore(start)) continue;
                if (end != null && date.isAfter(end)) continue;

                JsonNode day = entry.getValue();
                HistoricalPrice price = new HistoricalPrice();
                price.setDate(date);
                price.setTimestamp(OffsetDateTime.of(date.atStartOfDay(), ZoneOffset.UTC));
                price.setOpen(new BigDecimal(day.path("1. open").asText()));
                price.setHigh(new BigDecimal(day.path("2. high").asText()));
                price.setLow(new BigDecimal(day.path("3. low").asText()));
                price.setClose(new BigDecimal(day.path("4. close").asText()));
                price.setVolume(day.path("5. volume").asInt());
                prices.add(price);
            }

            HistoricalResponse response = new HistoricalResponse();
            response.setSymbol(symbol);
            response.setPrices(prices);
            return response;

        } catch (Exception e) {
            return null;
        }
    }
}
