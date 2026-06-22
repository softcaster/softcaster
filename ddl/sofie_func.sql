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

CREATE OR REPLACE FUNCTION fn_manage_ref_id()
RETURNS TRIGGER AS $$
DECLARE
    current_status_code VARCHAR;
BEGIN
    -- Recuperiamo il codice dello stato per il controllo
    SELECT code INTO current_status_code FROM txn_status WHERE id_txn_status = NEW.txn_status;

    IF (TG_OP = 'INSERT') THEN
        -- Se è PENDING e l'utente esiste un refId, usiamo l'ID appena generato
        IF current_status_code = 'PENDING' AND NEW.ref_id IS NULL THEN
            NEW.ref_id := NEW.id_financial_txn;
        END IF;

    ELSIF (TG_OP = 'UPDATE') THEN
        -- Se lo stato è PENDING, proteggiamo il campo (l'utente non può forzarlo)
        IF current_status_code = 'PENDING' AND NEW.ref_id IS NULL THEN
            NEW.ref_id := NEW.id_financial_txn;
        END IF;
        -- Se lo stato NON è PENDING, il trigger non entra nell'IF e 
        -- accetta il valore che l'utente ha messo nel campo refId.
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_financial_txn_ref_id
BEFORE INSERT OR UPDATE ON financial_txn
FOR EACH ROW
EXECUTE FUNCTION fn_manage_ref_id();
ALTER FUNCTION fn_manage_ref_id() OWNER TO sofie;
