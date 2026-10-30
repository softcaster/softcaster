-- ============================================================================
-- Volatilita' implicite per sottostante
--
--   volatility_surface        una per sottostante (coppia di cambi, scadenza di
--    │                        future, titolo...): codice e descrizione
--    └─ volatility_point      per data di rilevazione: scadenza, strike (NULL =
--                             ATM), volatilita' annua in decimale, fonte
--
-- Lettura: punti dell'ultima data di rilevazione non successiva alla data di
-- valutazione. Interpolazione nell'engine (GridVolatilitySurface): lineare
-- nello strike per scadenza, lineare nella varianza totale tra le scadenze,
-- piatta fuori dalla griglia. Usata da accumulator (MtM e pricing senza
-- volatilita' indicata) e dal prezzo teorico delle opzioni.
-- ============================================================================

BEGIN;

CREATE TABLE volatility_surface (
    id_volatility_surface  INTEGER NOT NULL,
    code                   VARCHAR(25) NOT NULL,
    description            VARCHAR(255) NOT NULL DEFAULT '',
    underlying             INTEGER NOT NULL,
    CONSTRAINT pk_volatility_surface PRIMARY KEY (id_volatility_surface),
    CONSTRAINT uq_volatility_surface_code UNIQUE (code),
    CONSTRAINT uq_volatility_surface_underlying UNIQUE (underlying),
    CONSTRAINT fk_volatility_surface_underlying
        FOREIGN KEY (underlying) REFERENCES master_data (id_master_data)
);
ALTER TABLE volatility_surface OWNER TO sofie;

CREATE SEQUENCE IF NOT EXISTS volatility_surface_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE volatility_surface_s OWNER TO sofie;

CREATE TABLE volatility_point (
    id_volatility_point    INTEGER NOT NULL,
    volatility_surface     INTEGER NOT NULL,
    reference_date         DATE NOT NULL,                   -- data di rilevazione
    expiry_date            DATE NOT NULL,                   -- scadenza dell'opzione
    strike                 NUMERIC(18,8),                   -- NULL = at the money
    volatility             NUMERIC(12,8) NOT NULL,          -- annua, decimale (0.08 = 8%)
    source                 VARCHAR(64) NOT NULL DEFAULT '',
    CONSTRAINT pk_volatility_point PRIMARY KEY (id_volatility_point),
    CONSTRAINT fk_volatility_point_surface
        FOREIGN KEY (volatility_surface) REFERENCES volatility_surface (id_volatility_surface) ON DELETE CASCADE,
    CONSTRAINT uq_volatility_point UNIQUE NULLS NOT DISTINCT (volatility_surface, reference_date, expiry_date, strike),
    CONSTRAINT ck_volatility_point_values
        CHECK (volatility > 0 AND volatility < 5 AND (strike IS NULL OR strike > 0) AND expiry_date > reference_date)
);
CREATE INDEX idx_volatility_point_date ON volatility_point (volatility_surface, reference_date);
ALTER TABLE volatility_point OWNER TO sofie;

CREATE SEQUENCE IF NOT EXISTS volatility_point_s START WITH 1 INCREMENT BY 1;
ALTER SEQUENCE volatility_point_s OWNER TO sofie;

COMMIT;
