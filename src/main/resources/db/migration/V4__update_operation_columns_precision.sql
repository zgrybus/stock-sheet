ALTER TABLE operations
ALTER
COLUMN price_per_volume TYPE DECIMAL(19, 4) USING (price_per_volume::DECIMAL(19, 4));

ALTER TABLE operations
ALTER
COLUMN total_price TYPE DECIMAL(19, 2) USING (total_price::DECIMAL(19, 2));

ALTER TABLE operations
ALTER
COLUMN volume TYPE DECIMAL(19, 4) USING (volume::DECIMAL(19, 4));