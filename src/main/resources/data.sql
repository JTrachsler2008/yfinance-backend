
INSERT INTO users (id, username, email, password, created_at) VALUES
(1, 'maxmuster', 'max@test.ch', 'test1234', '2026-01-01 00:00:00');

INSERT INTO portfolios (id, user_id, name, base_currency, description, created_at, updated_at) VALUES
(1, 1, 'Mein Portfolio', 'CHF', 'Hauptportfolio', '2020-01-01 00:00:00', '2026-01-01 00:00:00');

INSERT INTO accounts (id, portfolio_id, name, currency, cash_amount) VALUES
(1, 1, 'USD Konto',  'USD', 4820.00),
(2, 1, 'CHF Konto',  'CHF', 3200.00);

-- 10 Top-Aktien mit Sektor-Daten (Split-adjustierte Preise)
INSERT INTO securities (id, symbol, isin, name, asset_type, exchange_code, trading_currency, country_code, sector, created_at, updated_at) VALUES
(1,  'AAPL',  'US0378331005', 'Apple Inc.',            'STOCK', 'NASDAQ', 'USD', 'US', 'Technology',            '2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(2,  'MSFT',  'US5949181045', 'Microsoft Corporation', 'STOCK', 'NASDAQ', 'USD', 'US', 'Technology',            '2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(3,  'NVDA',  'US67066G1040', 'NVIDIA Corporation',    'STOCK', 'NASDAQ', 'USD', 'US', 'Technology',            '2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(4,  'GOOGL', 'US02079K3059', 'Alphabet Inc.',         'STOCK', 'NASDAQ', 'USD', 'US', 'Communication Services','2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(5,  'AMZN',  'US0231351067', 'Amazon.com Inc.',       'STOCK', 'NASDAQ', 'USD', 'US', 'Consumer Cyclical',     '2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(6,  'META',  'US30303M1027', 'Meta Platforms Inc.',   'STOCK', 'NASDAQ', 'USD', 'US', 'Communication Services','2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(7,  'TSLA',  'US88160R1014', 'Tesla Inc.',            'STOCK', 'NASDAQ', 'USD', 'US', 'Consumer Cyclical',     '2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(8,  'V',     'US92826C8394', 'Visa Inc.',             'STOCK', 'NYSE',   'USD', 'US', 'Financial Services',    '2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(9,  'JPM',   'US46625H1005', 'JPMorgan Chase & Co.',  'STOCK', 'NYSE',   'USD', 'US', 'Financial Services',    '2020-01-01 00:00:00', '2026-01-01 00:00:00'),
(10, 'NESN',  'CH0012221716', 'Nestlé S.A.',           'STOCK', 'SIX',    'CHF', 'CH', 'Consumer Defensive',    '2020-01-01 00:00:00', '2026-01-01 00:00:00');

-- Käufe zu historischen Kursen (2020-2021, Split-adjustiert)
-- AAPL: Kauf Jan 2021 @ 132 → heute ~220 (+67%)
-- MSFT: Kauf Feb 2021 @ 232 → heute ~440 (+90%)
-- NVDA: Kauf Apr 2021 @ 145 → heute ~1100 (+658%)  ← der Star
-- GOOGL: Kauf Jun 2021 @ 126 (post-split adj.) → heute ~190 (+51%)
-- AMZN: Kauf Aug 2021 @ 168 (post-split adj.) → heute ~220 (+31%)
-- META: Kauf Mär 2021 @ 285 → heute ~700 (+146%)
-- TSLA: Kauf Jan 2021 @ 230 (post-split adj.) → heute ~350 (+52%)
-- V:    Kauf Sep 2020 @ 207 → heute ~370 (+79%)
-- JPM:  Kauf Okt 2020 @ 104 → heute ~270 (+160%)
-- NESN: Kauf Jan 2020 @ 98 CHF → heute ~94 CHF (-4%)
INSERT INTO transactions (id, account_id, security_id, transaction_type, quantity, price, transaction_currency, fx_rate_to_portfolio, transaction_date) VALUES
(1,  1, 1,  'BUY',      25,  132.00, 'USD', 0.90, '2021-01-15'),
(2,  1, 2,  'BUY',      10,  232.00, 'USD', 0.90, '2021-02-10'),
(3,  1, 3,  'BUY',      20,  145.00, 'USD', 0.90, '2021-04-05'),
(4,  1, 4,  'BUY',      15,  126.00, 'USD', 0.90, '2021-06-01'),
(5,  1, 5,  'BUY',      12,  168.00, 'USD', 0.90, '2021-08-20'),
(6,  1, 6,  'BUY',       8,  285.00, 'USD', 0.90, '2021-03-15'),
(7,  1, 7,  'BUY',      15,  230.00, 'USD', 0.90, '2021-01-28'),
(8,  1, 8,  'BUY',      10,  207.00, 'USD', 0.91, '2020-09-10'),
(9,  1, 9,  'BUY',      20,  104.00, 'USD', 0.91, '2020-10-05'),
(10, 2, 10, 'BUY',      25,   98.00, 'CHF', 1.00, '2020-01-20'),
-- Dividenden
(11, 1, 1,  'DIVIDEND',  25,   0.96, 'USD', 0.89, '2024-02-15'),
(12, 1, 2,  'DIVIDEND',  10,   2.94, 'USD', 0.89, '2024-03-14'),
(13, 1, 8,  'DIVIDEND',  10,   2.08, 'USD', 0.89, '2024-09-02'),
(14, 1, 9,  'DIVIDEND',  20,   1.25, 'USD', 0.89, '2024-10-01'),
(15, 2, 10, 'DIVIDEND',  25,   2.80, 'CHF', 1.00, '2024-04-26');

-- Positionen (Gesamtbestand)
INSERT INTO positions (id, account_id, security_id, total_quantity, average_purchase_price) VALUES
(1,  1, 1,  25,  132.00),
(2,  1, 2,  10,  232.00),
(3,  1, 3,  20,  145.00),
(4,  1, 4,  15,  126.00),
(5,  1, 5,  12,  168.00),
(6,  1, 6,   8,  285.00),
(7,  1, 7,  15,  230.00),
(8,  1, 8,  10,  207.00),
(9,  1, 9,  20,  104.00),
(10, 2, 10, 25,   98.00);

INSERT INTO fx_rates (id, base_currency, quote_currency, rate_date, rate, created_at) VALUES
(1, 'USD', 'CHF', '2020-01-01', 0.97, '2020-01-01 00:00:00'),
(2, 'USD', 'CHF', '2021-01-01', 0.89, '2021-01-01 00:00:00'),
(3, 'USD', 'CHF', '2022-01-01', 0.92, '2022-01-01 00:00:00'),
(4, 'USD', 'CHF', '2023-01-01', 0.92, '2023-01-01 00:00:00'),
(5, 'USD', 'CHF', '2024-01-01', 0.86, '2024-01-01 00:00:00'),
(6, 'USD', 'CHF', '2025-01-01', 0.90, '2025-01-01 00:00:00'),
(7, 'USD', 'CHF', '2026-01-01', 0.89, '2026-01-01 00:00:00'),
(8, 'USD', 'CHF', '2026-06-01', 0.89, '2026-06-01 00:00:00'),
(9, 'CHF', 'USD', '2026-06-01', 1.12, '2026-06-01 00:00:00');

ALTER SEQUENCE users_seq RESTART WITH 100;
ALTER SEQUENCE portfolios_seq RESTART WITH 100;
ALTER SEQUENCE accounts_seq RESTART WITH 100;
ALTER SEQUENCE securities_seq RESTART WITH 100;
ALTER SEQUENCE transactions_seq RESTART WITH 100;
ALTER SEQUENCE positions_seq RESTART WITH 100;
ALTER SEQUENCE fx_rates_seq RESTART WITH 100;
