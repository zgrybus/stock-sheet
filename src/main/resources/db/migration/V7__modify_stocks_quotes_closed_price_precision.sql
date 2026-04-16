ALTER TABLE stocks_quotes
ALTER
COLUMN closed_price TYPE DECIMAL(19, 4) USING (closed_price::DECIMAL(19, 4));