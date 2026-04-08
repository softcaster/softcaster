CREATE OR REPLACE PROCEDURE upsert_instrument_quote()
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO instrument_quote_hist (id_instrument_quote, master_data, code, bid, ask,update_date)
    SELECT id_instrument_quote, master_data, code, bid, ask, CURRENT_DATE
    FROM instrument_quote
    ON CONFLICT (id_instrument_quote,update_date) -- <--- Qui PostgreSQL intercetta il duplicato
    DO UPDATE SET 
        master_data = EXCLUDED.master_data,
        code = EXCLUDED.code,
        bid = EXCLUDED.bid,
        ask = EXCLUDED.ask,
END;
$$;
-- call upsert_instrument_quote()

CREATE OR REPLACE FUNCTION aggiorna_id_instrument_quote_hist() RETURNS TRIGGER AS $aggiorna_id_instrument_quote_hist$
BEGIN
    IF (TG_OP = 'INSERT') THEN
       IF(NEW.id_instrument_quote_hist IS NULL) THEN
          NEW.id_instrument_quote_hist = nextval('instrument_quote_hist_s');
        END IF;
    END IF;

    RETURN NEW; -- this is important for a trigger
    END;
$aggiorna_id_instrument_quote_hist$ LANGUAGE plpgsql STABLE;

CREATE TRIGGER aggiorna_id_instrument_quote_hist BEFORE INSERT ON instrument_quote_hist
    FOR EACH ROW EXECUTE PROCEDURE aggiorna_id_instrument_quote_hist();
ALTER FUNCTION aggiorna_id_instrument_quote_hist() OWNER TO easypricer;

CREATE OR REPLACE FUNCTION upsert_instrument_quote()
RETURNS void AS $$
BEGIN
    INSERT INTO instrument_quote_hist (instrument_quote, master_data, code, bid, ask, update_date)
    SELECT id_instrument_quote, master_data, code, bid, ask, CURRENT_DATE
    FROM instrument_quote
    ON CONFLICT (instrument_quote,update_date) 
    DO UPDATE SET 
        instrument_quote = EXCLUDED.instrument_quote,
        master_data = EXCLUDED.master_data,
        code = EXCLUDED.code,
        bid = EXCLUDED.bid,
        ask = EXCLUDED.ask;
END;
$$ LANGUAGE plpgsql;
ALTER FUNCTION upsert_instrument_quote() OWNER TO easypricer;

-- SELECT upsert_instrument_quote();