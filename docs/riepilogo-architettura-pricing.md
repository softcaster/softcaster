# Pricing strumenti finanziari — riepilogo architettura e decisioni

*Aggiornato al 23 ottobre 2026 (sera). Riferimento per le nuove chat del progetto.*
*Progetto locale: `C:\tools\projects\DEVELOP\softcaster` — database PostgreSQL `sofie` su localhost.*

---

## 1. Architettura

| Livello | Jar | Responsabilità |
|---|---|---|
| REST | easy_pricer_srv | Due DTO unici senza sottoclassi in `mds_core.dto`: `PricingRequest` (date `LocalDate`, "yyyy-MM-dd" nel JSON) e `PricingResponse` (campi `Double` nullable, `@JsonInclude(NON_NULL)`: ogni calcolatore valorizza solo ciò che calcola); l'endpoint sceglie il calcolatore |
| Intermedio | **mds_core** (Spring, JPA) | Legge anagrafica e dati di mercato, risolve curve e prezzi, costruisce gli input tipizzati, chiama i pricer, converte i risultati nei DTO |
| Calcolo | **engine** (Spring per i bean, nessuna dipendenza da DB/entity) | Pricer come funzioni pure `input → output` |
| Anagrafica | **core** (`easy_pricer_core`) | Entity JPA, repository, DAO |

- Le dipendenze vanno in un solo verso: **mds_core → engine**. Le interfacce usate da entrambi (es. `DiscountCurve`) sono definite nell'engine.
- **L'engine riceve dati già risolti**: curve come oggetti, prezzi come numeri. I codici (es. `"TERMESTR"`) li risolve mds_core.
- Validazione degli input nei record dell'engine; errori di dati come eccezioni (`NoSuchElementException` → 404, `IllegalArgumentException` → 400), mai `null` o `0` come "non disponibile".
- Bean: i pricer sono creati in `EngineAutoConfiguration`, i calculator in `MdsCoreAutoConfiguration` (`@AutoConfiguration(after = EngineAutoConfiguration.class)`), con **iniezione via costruttore**. Una classe non deve essere sia `@Service` sia creata con `@Bean`.
- Build: Maven di NetBeans (`C:\tools\netbeans\java\maven\bin\mvn.cmd -o`), ordine engine → core → mds_core → moduli applicativi. `TestPricer` (test di mds_core) si lancia con `exec-maven-plugin:3.1.0:exec`, `classpathScope=test`.

## 2. Strutture di input e output (engine)

- Interfaccia unica e generica:
  ```java
  public interface Pricer<I extends PricingInput, O extends PricingResult> { O calculate(I input); }
  ```
  Attenzione ai *raw type*: sempre `implements Pricer<XRBInput, XRBResult>` / `extends AbstractFixedIncomePricer<XRBInput, XRBResult>`.
- Gerarchie `sealed` di **record** (stesso package dell'interfaccia sealed):
  - `PricingInput` → `CashFlowInput` (`XRBInput`, `FRBInput`, deposit, loan), `ForwardInput` (`FxForwardInput`, `BondForwardInput`), `OptionInput` (`EquityOptionInput`, `FxOptionInput`, `ForwardOptionInput`).
  - `PricingResult` → `CashFlowResult`, `ForwardResult` (`FxForwardResult`, `BondForwardResult`), `OptionResult`.
- Composizione invece di ereditarietà: `CashFlowSchedule` (flussi + convenzioni) riusato da XRB/FRB; `FloatingCouponSpec` per la parte variabile; i pricer simili sono "fratelli" sotto una classe astratta generica o usano un calcolatore comune.
- Record: copia difensiva delle liste (`List.copyOf`); nel compact constructor validare **i parametri**, non `this`.
- Progetto su **Java 17**: niente pattern matching nello `switch`, si usa `instanceof` con binding.
- Dispatcher in mds_core: un punto unico che sceglie il pricer per tipo di input.

## 3. Curve

- `DiscountCurve` (engine): `getDiscountFactor(LocalDate)`; implementazione `YieldCurve` con nodi DF e interpolazione log-lineare.
- Un **tasso singolo** si passa come `YieldCurve` a un nodo (DF = 1/(1 + r·t) alla data finale).
- Evitare `YieldCurve.update()` sulle curve passate all'engine: per bump e scenari creare una curva nuova.
- Bug noto da correggere: estrapolazione con la sola curva al giorno 0 → `0/0 = NaN`.
- **BTP**: curve di sconto = curva ECB + spread calibrato sugli z-spread (`spread_curve`). Lo z-spread rispetto alla curva ECB è lo spread emittente; rispetto alla spread curve è il residuo ricco/povero.
- Collegamento curve ↔ strumenti: tabella `ac_dc_bcy_link` (asset class, valuta, `curve_role`) → `yield_curve` **oppure** `spread_curve` (due colonne + `CHECK num_nonnulls(...) = 1`, chiave univoca `(asset_class, currency, curve_role)`). Entity `AcDcBcyLink` con FK come `Integer`, DAO con `get(...)` obbligatorio e `saveOrUpdate` sulla chiave naturale.
- `CurveRole`: `DISCOUNT`, `PROJECTION`, `REPO`, (`CREDIT_SPREAD`, `FX_BASIS` solo se servono). Ruolo = uso, tipo tabella = costruzione: sono indipendenti.

## 4. Pricer e convenzioni

### Strumenti a flussi (`CashFlowHelper`)
- **Tassi sempre in decimale** nell'engine (0,0425); conversione in percentuale solo nei DTO.
- Una sola funzione di sconto (`pvAtYield`) per prezzo, yield, duration e convexity; modified duration e convexity per **differenze centrali** (±0,1 bp), coerenti con qualunque compounding.
- Schedule costruito con daycount **del rateo** del titolo (`accrualDaycount`), frequenza e compounding del titolo.
- **DV01** = (clean + rateo) × modified duration × 0,0001, per 100 di nominale. Corretto in `XRBPricer` (prima, per precedenza degli operatori, valeva clean + rateo × MD × 0,0001 ≈ 100).
- Z-spread: spread continuo su tempo ACT/365.
- Punti aperti: rateo che tratta come ACT/ACT ogni daycount diverso da ACT/360; `Frequency.getYearFraction()` restituisce in realtà i periodi per anno.

### FX forward (`FxForwardPricer`, `FxForwardCalculator`)
- `valuationDate` = data di regolamento spot (oggi + business days), convenzione usata per tutti gli strumenti.
- `F = S · DF_estero / DF_domestico`, con DF calcolati come rapporto tra valuationDate e consegna.
- Consegna = `delivery_date` della scadenza; se assente, regolamento spot dell'ultimo giorno di negoziazione.
- Contratto facoltativo: `ForwardContract` (PV scontato) o `FutureContract` (regolato a margini, PV non scontato) — record dell'engine, non entity.

### Bond future — Mini BTP 10y Euronext (`XRBForwardPricer`, `BondForwardCalculator`)
- Specifiche: nozionale 25.000 EUR, cedola nozionale 6%, **regolamento per cassa**, EDSP = prezzo CTD / CF; expiry due giorni di borsa prima del 10 del mese; paniere BTP fissi 8,5–11 anni. **Tick 0,01** (0,01% del nominale, in unità di prezzo). **Multiplier 0,01** come i titoli: controvalore = prezzo × 0,01 × 25.000 (1 contratto a 111 = 27.750 €).
- Data a termine = expiry + T+2 (data valuta del prezzo del CTD a scadenza).
- Capitalizzazione con la **curva repo** (`P(val)/P(del)` per il titolo, `P(pagamento)/P(del)` per le cedole); `termRepoRate` e implied repo in interesse semplice ACT/360.
- CTD: prezzo teorico minimo; se c'è il prezzo quotato, implied repo massimo. Net basis, gross basis, implied repo per titolo in `DeliverableAnalysis`.
- `DeliverableAnalysis` contiene anche, al prezzo a pronti: **`yield`** (rendimento a scadenza, IRR del titolo), **`modifiedDuration`**, **`dv01`**. `XRBForwardResult.futureDv01()` = DV01 del CTD / CF (per 100 di nominale). Esposti anche nel REST (`ctdYield`, `ctdModifiedDuration`, `ctdDv01`, `futureDv01`); l'MtM (`BondFutureEvaluator`) salva `futureDv01()` in `instrument_valuation.dv01`.
- Il CTD si sceglie nell'engine; mds_core carica solo paniere, prezzi e CF.

### Opzioni
| Pricer | Input | Esercizio |
|---|---|---|
| `BlackAndScholesPricer` (con dividend yield) | `EquityOptionInput` | europeo |
| `GarmanKohlhagenPricer` | `FxOptionInput` | europeo |
| `Black76Pricer` | `ForwardOptionInput` | europeo |
| `CRRBinomialPricer` (nodi centrati sul forward, mai p fuori [0,1]) | tutti | europeo, americano, bermudan |

- `OptionContract` (tipo, stile, strike, scadenza, nozionale, date bermudan) separato dalla volatilità; `withVolatility()` per la vol implicita.
- `ImpliedVolatility.solve(pricer, input, prezzo)`: Newton protetto da intervallo, limiti di arbitraggio calcolati col modello.
- Greche: vega per punto di vol, theta per giorno, rho per punto percentuale; in Black-76 delta/gamma rispetto al **future**.
- `ForwardOptionInput.premiumDiscounted`: `true` se il premio è pagato subito (equity-style), `false` se futures-style.
- Enum: `ExerciseStyle` (EUROPEAN, AMERICAN, BERMUDAN), `PremiumStyle` (EQUITY_STYLE, FUTURES_STYLE), entrambi `IdentifiableEnum`.

### Opzioni CME su currency future
- **Black-76** sul prezzo del future trimestrale sottostante; opzioni **europee** dal 2017; scadenza 10:00 New York; le mensili esercitano nel trimestrale più vicino.
- Premio scontato con la curva USD (term SOFR): **confermato** confrontando gli strike in the money con i settlement CME.

## 5. Validazioni fatte

| Caso | Risultato |
|---|---|
| Bond (yield, duration) | Identici a Borsa Italiana |
| Black-Scholes S=K=100, r=5%, σ=20%, T=1 | Call 10,4506, put 5,5735; greche = differenze finite |
| Black-76 / Garman-Kohlhagen | Coincidono con BS sul forward equivalente; parità put-call |
| CRR 500 step | Put americana 6,0915 (rif. ≈ 6,09); europee ≈ BS |
| Mini BTP 10y dic-2026 (19/10/2026) | Teorico 110,856, CTD IT0005631590; mercato denaro 110,72 / lettera 111,72 → dentro la forchetta. Implied repo 3,42% vs term €STR 2,51% sull'ultimo prezzo 111 (poco liquido). CTD: yield 4,498%, MD 7,17, DV01 0,0681; DV01 future 0,0803 per 100 (≈ 20,07 €/bp a contratto). Ripetuto dopo ogni ristrutturazione dell'anagrafica: invariato |
| Opzione CME EUU dic-2026 call 1,135 | Con F = 1,12485 e σ = 6,41% riproduce il CME; il CME usa ≈ 43,7 giorni (settlement del 21/10 → 4/12 ore 10:00 NY) |

Lezioni: usare prezzi aggiornati e contemporanei; future e opzioni dallo **stesso settlement**; la data di valutazione è la data dei settlement.

## 6. Anagrafica e database

### Principio
**Una riga di `master_data` = uno strumento negoziabile.** Su questo si reggono la terna (controparte, posizione, anagrafica) di `position_detail`, `financial_txn`, quotazioni (`instrument_quote.master_data`), valutazioni (`instrument_valuation`, id = id_master_data) e contabilità. I dati comuni a più strumenti stanno in tabelle di prodotto **fuori** da `master_data`.

> Scartato: testata in `master_data` + scadenza negoziabile in `listed_contract` (avrebbe richiesto una quarta chiave in tutta la terna).

### Gerarchia (JOINED)
```
master_data                 codice, descrizione, valuta, asset class, issue/maturity date, business_days, multiplier
 ├─ cash_flow_master_data   strumenti con flussi: type_of_interest, form, daycount, accrual_daycount, frequency,
 │   │                      roll_convention, accrual_schedule_type, interest_rate, issue_price, redempion_price,
 │   │                      amortization_schedule, compounding; piano dei flussi (cash_flow_item, cash_flow_reset)
 │   ├─ security_master_data -> flt_security_master_data
 │   └─ loan_master_data
 ├─ future_master_data      scadenza: isin, contract_month, last_trading_date, delivery_date
 │   ├─ bond_future_master_data   future_product -> bond_future_product; paniere (deliverable_bonds)
 │   ├─ fx_future_master_data     future_product -> fx_future_product
 │   ├─ mm_future_master_data     future_product -> mm_future_product
 │   └─ cmd_future_master_data    future_product -> cmd_future_product
 │         └─ power_future_master_data (periodo di consegna in commodity_delivery_profile)
 ├─ option_master_data      serie: option_product (EAGER), underlying -> master_data, option_type, strike, isin;
 │                          scadenza = maturity_date
 └─ forex_master_data
```
- Classi: `MasterData`, `CashFlowMasterData` (astratta), `SecurityMasterData`, `FltSecurityMasterData`, `LoanMasterData`, `FutureMasterData` (astratta) e sottoclassi.
- FK della gerarchia: `security_master_data`/`loan_master_data` → `cash_flow_master_data` → `master_data`; flussi → `cash_flow_master_data`.

### Testate dei future (`future_product`, JOINED)
```
future_product        code (MBTP, M6E), descrizione, exchange_contract_code, settlement_type,
 │                    contract_value, tick_size, initial_margin, maintenance_margin,
 │                    currency, asset_class, business_days, multiplier (default copiati nella scadenza)
 ├─ bond_future_product
 ├─ fx_future_product   underlying (forex_master_data)
 ├─ mm_future_product   underlying
 └─ cmd_future_product  commodity_type
       └─ power_future_product  load_type
```
- Classi `FutureProduct` (astratta) e sottoclassi; `FutureProductDAO.findByCode(code, Tipo.class)`.
- Ogni sottoclasse di scadenza punta alla testata del suo tipo (FK verso la sottotabella giusta) e lo verifica in `@PrePersist`. `FutureMasterData` espone i dati di testata con getter delegati (`getContractValue()`, `getInitialMargin()`, `getSettlementType()`, `getUnderlying()`, `getLoadType()`…): processori, MtM e contabilità non conoscono la testata.
- `XxxFutureMasterData.of(product)` crea una scadenza con i default della testata; `product.contractCode(mese)` dà il codice standard (MBTP + Z + 6 → `MBTPZ6`).
- Ricerca scadenza: `BondFutureMasterDataDAO.getByCodeOrIsin`, `FxFutureMasterDataDAO.getByCodeOrIsin` (per codice, poi per ISIN).
- Dati attuali: testate M6E (12.500, tick 0,0001, mult 1) e MBTP (25.000, tick 0,01, mult 0,01); scadenze M6EU6, M6EZ6, MBTPZ6 (ISIN IT0026145778), MBTPH7, MBTPM7.
- Import panieri (`DeliverableBondsImportMgr`, CSV: [0] codice testata, [1] ISIN scadenza, [2] ultimo giorno, …): scadenza cercata per ISIN; se manca la crea sotto la testata; testate non in anagrafica (es. MBUN) ignorate in silenzio. Paniere `Set<DeliverableBonds>`, `DeliverableBonds` uguale per ISIN.
- Finestre `master_data_mgr` dei future: il campo "codice contratto" è il codice della testata (`FutureProductHelper` la crea o ne aggiorna i dati di prodotto); il multiplier della scadenza viene dalla testata. La combo del daycount resta nel layout ma non è più usata (da togliere nel `.form`).

### Opzioni (`option_product` + `option_master_data`)
```
option_product        code (EUU), descrizione, exchange_contract_code, asset_class, currency,
                      exercise_style, premium_style, settlement_type, contract_value, tick_size,
                      multiplier, business_days, margini, expiry_time + expiry_time_zone (IANA),
                      future_product -> sottostante derivato (le serie puntano a una sua scadenza)
                      underlying     -> sottostante secco fisso (titolo, azione, coppia di cambi)
                      (al più uno dei due: CHECK)
```
- Nessuna sottoclasse: il tipo del sottostante della serie (`OptionMasterData.underlying`, una riga di `master_data`) sceglie il pricer (future → Black-76, forex → Garman-Kohlhagen, titolo → Black-76 sul forward, azione → Black-Scholes, americane → binomiale).
- `@PrePersist` della serie: il sottostante dev'essere una scadenza del future della testata (le mensili esercitano nella trimestrale) oppure il sottostante fisso della testata.
- `OptionMasterData.of(product)`. Codice e ISIN della serie sono una convenzione delle viste, non dell'anagrafica: `OptionDlg` (opzioni CME) li calcola con `cmeSeriesCode` → `EUUZ6_C1140` (cifre dello strike senza punto, almeno 4: 1,345 → `C1345`), campi di sola lettura; viste future (Eurex, IDEM, OTC) gestiranno codice e ISIN a modo loro; `getExpiryInstant()` = data della serie + orario e fuso della testata. DAO: `OptionProductDAO.findByCode`, `OptionMasterDataDAO.getByCodeOrIsin`, `findAllByProductAssetClass(List)`.
- Enum `ExerciseStyle`, `PremiumStyle` (nuovo; `isDiscounted()` per `ForwardOptionInput.premiumDiscounted`), `OptionType` con lookup `exercise_style`, `premium_style`, `option_type` e converter.
- Asset class per famiglia di sottostante: `FFO` (opzioni su FX future, creata), poi `BFO` (bond future), `FXO` (forex), `BDO` (titoli), `EQO` (azioni) quando servono.
- `master_data_mgr`: pannello `OptionPanel` (Fx Options = FFO+FXO, Bond Options = BFO+BDO) e finestra `OptionDlg` (tab Specification = serie, tab Product = testata, trovata o creata per codice come per i future; codice e ISIN della serie calcolati).
- EUU va su 6E (125.000 €); in anagrafica c'è solo M6E (12.500 €): stesso prezzo, nozionale diverso.

### Regole di caricamento (Hibernate)
- Le testate dei future sono `FetchType.EAGER` (poche righe, condivise; le loro associazioni restano LAZY). Le uniche associazioni EAGER della gerarchia.
- Spring Data usa gli entity graph come *fetch graph* (ignora gli EAGER fuori dal graph): nei repository delle anagrafiche si usa `type = EntityGraphType.LOAD` (base `BaseMasterDataRepository` e `findAll` dei future).
- Le liste delle finestre desktop (fuori transazione) devono caricare tutto ciò che la finestra legge: future FX/MM con testata → sottostante; titoli fissi e floating con emittente, flussi (e indice di riferimento per i floating) — graph `SecurityMasterData.fullGraphWithCashFlow`, `FltSecurityMasterData.fullGraphWithCashFlow`.
- Ogni `@NamedEntityGraph` dichiara i propri `@NamedSubgraph`; includere `calendar → holidays` quando serve il `Calendar`; **mai due collezioni in join se una è una `List`** (duplicati o `MultipleBagFetchException`) → `holidays` e paniere sono `Set`.

### Calendari
- `holiday` contiene solo giorno e mese (niente anno): le festività si ricaricano ogni anno. Limite accettato: i calcoli negli anni successivi usano le date mobili dell'anno caricato.
- Corretto: "New Year Day" EUR era 1/8 → 1/1 (DB e `sofie_startup.sql`).

### Script DDL (`ddl/`)
- `sofie.sql`, `sofie_acct.sql`, `sofie_func.sql`, `sofie_proc.sql` ricreano **esattamente** il DB (verificato: 1.694 oggetti, 0 differenze, nessun errore). **Fa fede il DB**: ogni modifica fatta su pgAdmin va riportata negli script.
- Migrazioni applicate il 23/10/2026: `future_product.sql`, `cash_flow_master_data.sql` (eliminate anche 5 righe orfane di `security_master_data`). Storicizzazione quotazioni (`upsert_instrument_quote`, trigger su `instrument_quote_hist`) in commento in `sofie_func.sql`: non presente sul DB.
- `option_product.sql` (lookup opzioni, asset class FFO, `option_product`, `option_master_data`): già riportata in `sofie.sql`/`sofie_startup.sql`; i servizi hanno `ddl-auto=validate`, quindi va applicata prima di avviarli con il nuovo core.
- `purge_expired_instruments.sql`: pulizia degli strumenti scaduti (con `dry_run`).
- `sofie_startup.sql` si ferma alla riga 360 (`system_business_calendar` senza `last_official_date`): errore preesistente.
- Vecchi import JDBC di easy_import: `Master_dataDAO` adattato (scrive `master_data` + `cash_flow_master_data`); `BondImportMgr`, `BondFutImportMgr`, `ForexImportMgr` non registrati, non aggiornati.

## 7. Prossimi passi

1. **Opzioni**: anagrafica fatta (sezione 6, migrazione `ddl/option_product.sql`). Restano: pricing REST e MtM (evaluator `FFO` con Black-76 sul future della serie, tempo alla scadenza da `getExpiryInstant()`); smile/volatilità implicite per testata, scadenza e strike in una tabella di dati di mercato; import dei settlement CME con calcolo e salvataggio della vol implicita.
2. **Fase B — MtM e dati di mercato**: verificare a runtime evaluator FX/CMD e MtM dei future con la nuova anagrafica; quotazioni per scadenza; tempo alla scadenza in frazioni di giorno usando orario e fuso della testata.
3. **Fase C — interfacce**: finestre di `master_data_mgr` (togliere la combo daycount dai future, scelta esplicita della testata), form React di `easy_pricer_cli` e JSON REST dei future.
4. **Test automatici** con i casi validati (Borsa Italiana, Mini BTP incl. yield/DV01 del CTD, CME). In `TestPricer` il test FX è commentato.
5. Punti aperti minori: scelta bid/mid/ask in `BondForwardCalculator.spotCleanPrice` (oggi BID), controllo data delle quotazioni, estrapolazione `YieldCurve`, rateo per daycount diversi, `Frequency.getYearFraction()`.
