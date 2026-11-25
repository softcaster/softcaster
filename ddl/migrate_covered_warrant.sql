-- ============================================================================
-- Covered warrant (CWT): opzioni cartolarizzate emesse da una banca, quotate
-- per warrant (SeDeX, EuroTLX). Plain vanilla, per ora solo su indice.
--
--   testata  covered_warrant_product, sottotipo di spot_option_product (JOINED):
--            emittente (issuer) e multiplo (ratio, unita' di sottostante per warrant)
--   serie    spot_option_master_data come le altre opzioni su strumento secco:
--            ISIN del warrant, call/put, strike, scadenza
--
-- Posizione in numero di warrant al prezzo del warrant (contract value 1,
-- multiplier 1); il ratio porta il premio per unita' di sottostante al prezzo
-- del warrant (OptionMasterData.getPriceRatio).
--
-- Da provare su una copia; applicata a sofie si rigenerano i file con
-- ddl/export_schema.ps1 e questo script si butta.
-- ============================================================================

BEGIN;

INSERT INTO public.asset_class (id_asset_class, super_class, code, description) VALUES
    (25, 4, 'CWT', 'Covered Warrant');
SELECT setval('public.asset_class_s', (SELECT max(id_asset_class) FROM public.asset_class), true);

INSERT INTO public.asset_class_script_mapping (asset_class_script_mapping_id, asset_class, event_type, script_code) VALUES
    (22, 25, NULL, 'OPTION');
SELECT setval('public.asset_class_script_mapping_s',
              (SELECT max(asset_class_script_mapping_id) FROM public.asset_class_script_mapping), true);

CREATE TABLE public.covered_warrant_product (
    id_option_product integer NOT NULL,
    issuer integer NOT NULL,
    ratio numeric(28,10) NOT NULL,
    CONSTRAINT pk_covered_warrant_product PRIMARY KEY (id_option_product),
    CONSTRAINT fk_covered_warrant_product_parent FOREIGN KEY (id_option_product) REFERENCES public.spot_option_product(id_option_product),
    CONSTRAINT fk_covered_warrant_product_issuer FOREIGN KEY (issuer) REFERENCES public.issuer(id_issuer),
    CONSTRAINT ck_covered_warrant_product_ratio CHECK ((ratio > (0)::numeric))
);

COMMIT;

SELECT ac.code, ac.description, sc.code AS super_class, m.script_code
FROM public.asset_class ac
JOIN public.super_class sc ON sc.id_super_class = ac.super_class
LEFT JOIN public.asset_class_script_mapping m ON m.asset_class = ac.id_asset_class
WHERE ac.code = 'CWT';
