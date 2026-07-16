# YFinance Portfolio Backend

Spring Boot 3.1.3 REST-API für das Portfolio-Management-System.

## Überblick

Dieses Backend stellt alle API-Endpunkte für die React-Frontend-App bereit. Preisdaten werden live von einem Python-Microservice (yfinance, Port 8000) abgerufen. Portfoliodaten (Transaktionen, Positionen, FX-Kurse) werden in einer H2-Datenbankdatei gespeichert.

## Technologie-Stack

- **Java 17** + **Spring Boot 3.1.3**
- **H2 File-based Database** (`data/testdb.mv.db`)
- **yFinance Python-Microservice** auf Port 8000
- **OpenAPI Generator** für den yFinance-Client
- **Lombok** + **SLF4J** Logging

## Setup & Start

### Voraussetzungen

1. Java 17+
2. Maven 3.8+
3. Python-Microservice läuft auf `http://localhost:8000`

### Backend starten

```bash
mvn spring-boot:run
```

API läuft auf `http://localhost:8080`.

### Python-Microservice starten

```bash
cd yfinance-service
pip install fastapi yfinance uvicorn
uvicorn main:app --port 8000
```

## API-Endpunkte

### Portfolios
| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| GET | `/portfolios` | Alle Portfolios |
| POST | `/portfolios` | Portfolio erstellen |
| PATCH | `/portfolios/{id}/currency?currency=CHF` | Basiswährung ändern |

### Performance
| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| GET | `/portfolios/{id}/performance?currency=CHF` | Kennzahlen (Gesamtwert, Gewinn, Dividenden) |
| GET | `/portfolios/{id}/history?months=36&currency=CHF&from=2023-01-01&to=2024-01-01` | Historischer Verlauf |
| GET | `/portfolios/{id}/realized-gains` | Realisierte Gewinne inkl. Gebühren |
| GET | `/portfolios/{id}/dividends?currency=CHF` | Dividendenerträge pro Jahr |

### Transaktionen
| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| POST | `/accounts/{id}/transactions` | Transaktion erstellen (Felder: `fee`, `tax` optional) |
| GET | `/accounts/{id}/transactions` | Transaktionshistorie |

### Risikoanalyse
| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| GET | `/portfolios/{id}/risk` | Volatilität, Sharpe Ratio, Beta, Max Drawdown, VaR |

### Vergleiche
| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| GET | `/compare/asset-classes` | Normierter 10-Jahres-Vergleich (SPY, QQQ, Gold, Anleihen, Bitcoin) |
| GET | `/compare/benchmark?symbol=SPY&months=36` | Einzelner Benchmark-Verlauf |
| GET | `/compare/risk-benchmarks` | Rendite/Volatilität für Index-Benchmarks (für Risiko-Bubble-Chart) |
| GET | `/compare/portfolios?p1=SPY:60,AGG:40&p2=QQQ:100` | Zwei Portfolio-Konfigurationen vergleichen |

### Sparplan-Simulation
| Methode | Pfad | Beschreibung |
|---------|------|-------------|
| GET | `/simulate/sparplan?startDate=2015-01-01&betrag=500&intervallMonate=1&positionen=SPY:60,QQQ:40` | Sparplan simulieren, gibt CAGR, Max Drawdown, Endwert und Chart-Daten zurück |

## Tests

```bash
mvn test
```

Tests befinden sich in `src/test/java/ch/allianz/jt/service/`:
- `AccountServiceImplTest` — Einzahlung, Auszahlung, InsufficientFunds
- `TransactionServiceImplTest` — BUY/SELL/DIVIDEND mit Gebühren, Durchschnittspreis-Berechnung
