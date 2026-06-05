package ch.allianz.jt.controller;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.generated.api.NewsApi;
import ch.allianz.jt.generated.model.NewsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewsController implements NewsApi {

    private final YFinanceClient client;

    public NewsController(final YFinanceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<NewsResponse> getNewsBySymbol(final String symbol,
                                                        final Integer count,
                                                        final String tab) {
        NewsResponse response = client.getNews(symbol, count, tab);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
