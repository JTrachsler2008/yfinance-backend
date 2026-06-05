
# TODO

## ✅ Erledigt
* **Service Interfaces** — User, Portfolio, Account, Security, Transaction, FxRate
* **Generated Package** — OpenAPI-Client für yFinance

## 🔜 Offen

### MarketController
Java Backend als Gateway zum yFinance Python-Service (localhost:8000).

Endpoints:
- `GET /market/quote/{symbol}` — aktueller Kurs
- `GET /market/quote?symbols=` — Bulk-Kurse (mehrere Symbole)
- `GET /market/historical/{symbol}` — historische Kursdaten
- `GET /market/info/{symbol}` — Firmeninfos
- `GET /market/snapshot/{symbol}` — Info + Quote kombiniert
- `GET /market/news/{symbol}` — News-Artikel
- `GET /market/earnings/{symbol}` — Gewinnberichte

Bestehende Klassen nutzen:
- `YFinanceClient` (bereits vorhanden, ruft localhost:8000 auf)
- Generierte OpenAPI-Modelle in `ch.allianz.jt.generated`

### Performance-Service
- Marktwert eines Portfolios berechnen (Positionen × aktueller Kurs)
- Gewinn/Verlust pro Position
- Volatilität
- Endpoint: `GET /portfolios/{id}/performance`

### JUnit Tests
- Unit Tests für Services
- Integration Tests für Controller