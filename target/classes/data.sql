
INSERT INTO users (id, username, email, password, created_at) VALUES
(1, 'maxmuster', 'max@test.ch', 'test1234', '2026-01-01 00:00:00');

INSERT INTO portfolios (id, user_id, name, base_currency, description, created_at, updated_at) VALUES
(1, 1, 'Mein Portfolio', 'CHF', 'Hauptportfolio', '2026-01-01 00:00:00', '2026-01-01 00:00:00');


INSERT INTO accounts (id, portfolio_id, name, currency, cash_amount) VALUES
(1, 1, 'USD Konto', 'USD', 6105.00);


INSERT INTO securities (id, symbol, isin, name, asset_type, exchange_code, trading_currency, country_code, created_at, updated_at) VALUES
(1, 'AAPL', 'US0378331005', 'Apple Inc.',        'STOCK', 'NASDAQ', 'USD', 'US', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(2, 'MSFT', 'US5949181045', 'Microsoft Corp.',   'STOCK', 'NASDAQ', 'USD', 'US', '2026-01-01 00:00:00', '2026-01-01 00:00:00'),
(3, 'NESN', 'CH0012221716', 'Nestlé S.A.',        'STOCK', 'SIX',    'CHF', 'CH', '2026-01-01 00:00:00', '2026-01-01 00:00:00');

INSERT INTO transactions (id, account_id, security_id, transaction_type, quantity, price, transaction_currency, fx_rate_to_portfolio, transaction_date) VALUES
(1, 1, 1, 'BUY',      10,  182.00, 'USD', 0.89, '2026-01-10'),
(2, 1, 2, 'BUY',       5,  415.00, 'USD', 0.89, '2026-02-01'),
(3, 1, 1, 'DIVIDEND', 10,    2.50, 'USD', 0.89, '2026-03-15');

INSERT INTO positions (id, account_id, security_id, total_quantity, average_purchase_price) VALUES
(1, 1, 1, 10, 182.00),
(2, 1, 2,  5, 415.00);

INSERT INTO fx_rates (id, base_currency, quote_currency, rate_date, rate, created_at) VALUES
(1, 'USD', 'CHF', '2026-01-01', 0.89, '2026-01-01 00:00:00'),
(2, 'USD', 'CHF', '2026-06-01', 0.89, '2026-06-01 00:00:00'),
(3, 'CHF', 'USD', '2026-06-01', 1.12, '2026-06-01 00:00:00');

ALTER SEQUENCE users_seq RESTART WITH 100;
ALTER SEQUENCE portfolios_seq RESTART WITH 100;
ALTER SEQUENCE accounts_seq RESTART WITH 100;
ALTER SEQUENCE securities_seq RESTART WITH 100;
ALTER SEQUENCE transactions_seq RESTART WITH 100;
ALTER SEQUENCE positions_seq RESTART WITH 100;
ALTER SEQUENCE fx_rates_seq RESTART WITH 100;
