ALTER TABLE stocks
    ADD dividend DECIMAL(19, 4);

ALTER TABLE stocks
    ADD dividend_frequency VARCHAR(255);

ALTER TABLE stocks
    ADD price DECIMAL(19, 4);

UPDATE stocks
SET dividend = 0.0000
WHERE dividend IS NULL;
ALTER TABLE stocks
    ALTER COLUMN dividend SET NOT NULL;

UPDATE stocks
SET dividend_frequency = 'NONE'
WHERE dividend_frequency IS NULL;
ALTER TABLE stocks
    ALTER COLUMN dividend_frequency SET NOT NULL;

UPDATE stocks
SET price = 0.0000
WHERE price IS NULL;
ALTER TABLE stocks
    ALTER COLUMN price SET NOT NULL;