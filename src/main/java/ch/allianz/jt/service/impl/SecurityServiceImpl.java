package ch.allianz.jt.service.impl;

import ch.allianz.jt.client.YFinanceClient;
import ch.allianz.jt.entity.Security;
import ch.allianz.jt.generated.model.InfoResponse;
import ch.allianz.jt.repository.SecurityRepository;
import ch.allianz.jt.service.SecurityService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final SecurityRepository securityRepository;
    private final YFinanceClient yFinanceClient;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityServiceImpl(final SecurityRepository securityRepository, final YFinanceClient yFinanceClient) {
        this.securityRepository = securityRepository;
        this.yFinanceClient = yFinanceClient;
    }

    @Override
    public Security createSecurity(final Security security) {
        security.setCreatedAt(LocalDateTime.now());
        security.setUpdatedAt(LocalDateTime.now());
        return securityRepository.save(security);
    }

    @Override
    public List<Security> getAll() {
        return securityRepository.findAll();
    }

    @Override
    public Optional<Security> getById(final Long id) {
        return securityRepository.findById(id);
    }

    @Override
    public Optional<Security> getBySymbol(final String symbol) {
        return securityRepository.findBySymbol(symbol);
    }

    @Override
    public List<Security> refreshAll() {
        List<Security> all = securityRepository.findAll();
        for (Security sec : all) {
            if (sec.getSymbol() == null) continue;
            try {
                InfoResponse info = yFinanceClient.getInfo(sec.getSymbol());
                if (info != null) {
                    if (info.getSector() != null) sec.setSector(info.getSector());
                    if (info.getCountry() != null) sec.setCountryCode(info.getCountry());
                    if (info.getCurrency() != null && sec.getTradingCurrency() == null) sec.setTradingCurrency(info.getCurrency());
                    if (sec.getName() == null && info.getLongName() != null) sec.setName(info.getLongName());
                    securityRepository.save(sec);
                }
            } catch (Exception ignored) {}
        }
        return securityRepository.findAll();
    }

    @Override
    public Security lookupOrCreate(final String symbol) {
        String sym = symbol.toUpperCase().trim();
        return securityRepository.findBySymbol(sym).orElseGet(() -> {
            InfoResponse info = null;
            try { info = yFinanceClient.getInfo(sym); } catch (Exception ignored) {}
            Security sec = new Security();
            sec.setSymbol(sym);
            if (info != null) {
                sec.setName(info.getLongName() != null ? info.getLongName() : info.getShortName());
                sec.setTradingCurrency(info.getCurrency());
                sec.setCountryCode(info.getCountry());
                sec.setSector(info.getSector());
            } else {
                sec.setName(sym);
                sec.setTradingCurrency("USD");
            }
            sec.setAssetType("STOCK");
            sec.setCreatedAt(LocalDateTime.now());
            return securityRepository.save(sec);
        });
    }

    @Override
    public List<Map<String, String>> search(String query) {
        List<Map<String, String>> results = new ArrayList<>();
        try {
            String url = "https://query1.finance.yahoo.com/v1/finance/search?q=" +
                    java.net.URLEncoder.encode(query, "UTF-8") +
                    "&quotesCount=8&newsCount=0&enableFuzzyQuery=false";
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            headers.set("Accept", "application/json");
            var entity = new org.springframework.http.HttpEntity<>(headers);
            var response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode quotes = root.path("quotes");
            if (quotes.isArray()) {
                for (JsonNode q : quotes) {
                    String type = q.path("quoteType").asText("");
                    if (!type.equals("EQUITY") && !type.equals("ETF") && !type.equals("MUTUALFUND")) continue;
                    Map<String, String> item = new HashMap<>();
                    item.put("symbol", q.path("symbol").asText(""));
                    item.put("name", q.path("longname").asText(q.path("shortname").asText("")));
                    item.put("exchange", q.path("exchDisp").asText(""));
                    item.put("type", type);
                    results.add(item);
                }
            }
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("Security search fehlgeschlagen: {}", e.getMessage());
        }
        return results;
    }
}
