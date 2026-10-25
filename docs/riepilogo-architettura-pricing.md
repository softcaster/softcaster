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
 ├─ option_master_data      serie: option_product (EAGER), option_type, strike, isin; scadenza = maturity_date
 │   ├─ future_option_master_data  underlying -> future_master_data
 │   └─ spot_option_master_data    underlying -> master_data, delivery_date
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
- Finestre `master_data_mgr` dei future: prima si inserisce la testata da *Futures → Future Products* (`FutureProductPanel`/`FutureProductDlg`), poi la scadenza la sceglie dalla combo **Product** (in cima alla scheda Specification, solo testate del tipo della finestra; la finestra Cmd esclude le Power). I dati di prodotto nella finestra della scadenza sono in sola lettura; al salvataggio la scadenza copia dalla testata valuta, asset class, business days e multiplier (`FutureProductHelper.applyProduct`). `FutureProductDlg`: tipo (`ProductKind`: Bond/Fx/Mm/Cmd/Power, con asset class e default di business days e multiplier), codice, valuta, business days e multiplier fissati alla creazione; modificabili descrizione, exchange code (default = codice), regolamento, contract value, tick, margini (iniziale obbligatorio, NOT NULL), sottostante (Fx, Mm), commodity (Cmd, Power) e load type (Power). La cancellazione è ammessa solo per le testate senza scadenze né opzioni (`FutureProductRepository.countReferences`, query nativa perché la colonna `future_product` sta nelle sottoclassi; le FK delle scadenze puntano alla testata del proprio tipo, es. `bond_future_master_data` → `bond_future_product`, e il controllo serve a dare un messaggio chiaro invece dell'errore di vincolo). La combo del daycount resta nel layout ma non è più usata (da togliere nel `.form`).

### Opzioni (`option_product` + `option_master_data`, due rami JOINED)
```
option_product        code (EUU), descrizione, exchange_contract_code, asset_class, currency,
 │                    exercise_style, premium_style, settlement_type, contract_value, tick_size,
 │                    multiplier, business_days, margini, expiry_time + expiry_time_zone (IANA; anche cut OTC)
 ├─ future_option_product   future_product NOT NULL (EAGER): le serie puntano a una sua scadenza
 └─ spot_option_product     underlying (EAGER, facoltativo): strumento secco fisso di tutte le serie
```
- Due rami, come i future: vincoli `NOT NULL` nello schema, tipi espliciti (`FutureOptionMasterData.getUnderlying()` → `FutureMasterData`), campi propri (consegna delle opzioni spot). Il ramo sceglie il pricer: su future → Black-76; spot → Garman-Kohlhagen (cambi), Black-76 sul forward (titoli), Black-Scholes (azioni); americane → binomiale.
- `OptionMasterData` (astratta) mappa la testata (`option_product` sta nella tabella base) e la restituisce col tipo del ramo; `@PrePersist`: tipo di testata giusto e coerenza del sottostante (`checkUnderlying()` del ramo: scadenza del future della testata, oppure sottostante fisso della testata spot).
- `XxxOptionMasterData.of(product)`; `getExpiryInstant()` = data della serie + orario e fuso della testata. DAO: `OptionProductDAO.findByCode(code[, Tipo.class])`, `FutureOptionMasterDataDAO` / `SpotOptionMasterDataDAO` (`getByCodeOrIsin`, `findAllByProductAssetClass(List)`).
- Codice e ISIN della serie sono una convenzione delle viste, non dell'anagrafica: `FutureOptionDlg` (opzioni CME su future) li calcola con `cmeSeriesCode` → `EUUZ6_C1140` (cifre dello strike senza punto, almeno 4: 1,345 → `C1345`), campi di sola lettura; le viste delle opzioni spot (Eurex, IDEM, OTC) gestiranno codice e ISIN a modo loro.
- Enum `ExerciseStyle`, `PremiumStyle` (nuovo; `isDiscounted()` per `ForwardOptionInput.premiumDiscounted`), `OptionType` con lookup `exercise_style`, `premium_style`, `option_type` e converter.
- Asset class per famiglia di sottostante: `FFO` (opzioni su FX future), `FXO` (sul cambio), `BDO` (su titoli) create; `BFO` (bond future) ed `EQO` (azioni) quando servono.
- Niente foglie per tipo di sottostante (es. opzione su FX future): non aggiungerebbero campi; si introduce una foglia solo per un tipo con dati propri (asiatiche, quanto…). La coerenza famiglia/future sottostante è in `FutureOptionProduct.FUTURE_PRODUCT_TYPES` (FFO → `FxFutureProduct`, BFO → `BondFutureProduct`), verificata in `@PrePersist` e usata da `FutureOptionDlg`; per il ramo spot `SpotOptionProduct.UNDERLYING_TYPES` (FXO → `ForexMasterData`, BDO → `SecurityMasterData`), verificata su testata e serie. Una nuova famiglia va aggiunta lì.
- `master_data_mgr`: albero Options → Future Options (Fx = FFO, Bond = BFO) e Spot Options (Fx = FXO, Bond = BDO); `OptionPanel` per ramo e famiglia. `FutureOptionDlg`: underlying future e scadenze filtrati per famiglia, scadute escluse, codice e ISIN CME calcolati. `SpotOptionDlg`: codice e ISIN liberi, data di consegna proposta (scadenza + giorni di regolamento feriali, modificabile), sottostanti del tipo della famiglia, casella "Fixed on product" per il sottostante fisso della testata (Eurex) o testata generica (OTC). Tab Specification = serie, tab Product = testata trovata o creata per codice.
- `easy_pricer_mds`: `FxOptIQDlg` (quotazioni opzioni FX) usa `FutureOptionMasterDataDAO`.
- Dati: testata EUU (su M6E, 125.000, tick 0,00005, 09:00 America/Chicago), serie `EUUZ6_C1140` su M6EZ6. EUU va su 6E (125.000 €); in anagrafica c'è solo M6E (12.500 €): stesso prezzo, nozionale diverso.
### Regole di caricamento (Hibernate)
- Le testate dei future e delle opzioni sono `FetchType.EAGER` (poche righe, condivise), così come il future di `future_option_product` e il sottostante fisso di `spot_option_product`; le altre associazioni restano LAZY. Anche i repository delle testate usano graph `LOAD`.
- Spring Data usa gli entity graph come *fetch graph* (ignora gli EAGER fuori dal graph): nei repository delle anagrafiche si usa `type = EntityGraphType.LOAD` (base `BaseMasterDataRepository` e `findAll` dei future).
- Le liste delle finestre desktop (fuori transazione) devono caricare tutto ciò che la finestra legge: future FX/MM con testata → sottostante; titoli fissi e floating con emittente, flussi (e indice di riferimento per i floating) — graph `SecurityMasterData.fullGraphWithCashFlow`, `FltSecurityMasterData.fullGraphWithCashFlow`.
- Ogni `@NamedEntityGraph` dichiara i propri `@NamedSubgraph`; includere `calendar → holidays` quando serve il `Calendar`; **mai due collezioni in join se una è una `List`** (duplicati o `MultipleBagFetchException`) → `holidays` e paniere sono `Set`.

### Calendari
- `holiday` contiene solo giorno e mese (niente anno): le festività si ricaricano ogni anno. Limite accettato: i calcoli negli anni successivi usano le date mobili dell'anno caricato.
- Corretto: "New Year Day" EUR era 1/8 → 1/1 (DB e `sofie_startup.sql`).

### Script DDL (`ddl/`)
- `sofie.sql`, `sofie_acct.sql`, `sofie_func.sql`, `sofie_proc.sql` ricreano **esattamente** il DB (verificato: 1.694 oggetti, 0 differenze, nessun errore). **Fa fede il DB**: ogni modifica fatta su pgAdmin va riportata negli script.
- Migrazioni applicate il 23/10/2026: `future_product.sql`, `cash_flow_master_data.sql` (eliminate anche 5 righe orfane di `security_master_data`). Storicizzazione quotazioni (`upsert_instrument_quote`, trigger su `instrument_quote_hist`) in commento in `sofie_func.sql`: non presente sul DB.
- `option_product.sql` (lookup opzioni, asset class FFO, `option_product`, `option_master_data`), applicata il 24/10/2026; `option_branches.sql` (rami future/spot di testate e serie, migra i dati esistenti), applicata il 25/10/2026; `option_spot_asset_classes.sql` (asset class FXO, BDO). Entrambe riportate in `sofie.sql`/`sofie_startup.sql`; i servizi hanno `ddl-auto=validate`, quindi le migrazioni vanno applicate prima di avviarli con il nuovo core.
- `purge_expired_instruments.sql`: pulizia degli strumenti scaduti (con `dry_run`).
- `sofie_startup.sql` si ferma alla riga 360 (`system_business_calendar` senza `last_official_date`): errore preesistente.
- Vecchi import JDBC di easy_import: `Master_dataDAO` adattato (scrive `master_data` + `cash_flow_master_data`); `BondImportMgr`, `BondFutImportMgr`, `ForexImportMgr` non registrati, non aggiornati.

## 7. Prossimi passi

1. **Opzioni**: anagrafica fatta (sezione 6, migrazioni `ddl/option_product.sql` e `ddl/option_branches.sql`); finestre di entrambi i rami fatte. **Pricing delle opzioni fatto (tutti i rami)**: `mds_core/calc/OptionCalculator` (bean in `MdsCoreAutoConfiguration`), endpoint `POST /pricing/option`. La serie si cerca prima tra le opzioni su future, poi tra quelle spot; il modello lo sceglie la serie: su future (FFO, BFO) → `Black76Pricer` sul prezzo del future; FXO → `GarmanKohlhagenPricer` (spot della coppia, curve `ccyIrc`/`bcyIrc` della coppia o `domesticRCurve`/`foreignRCurve`); BDO → `Black76Pricer` sul prezzo secco a termine del titolo alla `deliveryDate` della serie, calcolato da `BondForwardCalculator.getSingleBondForwardInput` (paniere di un titolo con CF = 1, curva o tasso repo `domesticRCurve`/`domesticRate` ACT/360, premio scontato con la stessa curva o con il repo reso continuo); americane → `CRRBinomialPricer` (bermudan rifiutate: niente date di esercizio in anagrafica). Request: `isin` (serie), `referenceDate` (trade date, default data ufficiale), `referencePrice` (prezzo del sottostante: future, cambio, prezzo secco del titolo; se manca, quotazione in anagrafica), `volatility` oppure `optionPrice` (premio di mercato → vol implicita con `ImpliedVolatility`), curve/tassi come sopra (per le FX future option default curva `ccyIrc` della coppia, TERMSOFR per EURUSD); premio futures-style non scontato. Response: `optionModel`, `optionPremium` (unitario), `optionValue` (premio × contract value × multiplier: per le opzioni su titolo, premio per 100 di nominale, multiplier 0,01 come per i bond future), `volatility`, `underlyingForward`, `discountFactor`, `timeToExpiry`, greche. Tempo alla scadenza ACT/365 a giorni interi (valutazione di fine giornata): l'ora di scadenza (`getExpiryInstant()`) non è ancora usata. Verificato su EUUZ6_C1140 (F 1,16, vol 6,41%: premio 0,0229, delta 0,788; vol implicita ricostruita esatta; americana CRR 0,02293) e, su una copia del DB con serie di prova, su FXO EURUSD 1,17 (spot 1,16, vol 7%: call 0,00938, forward 1,1627) e BDO su IT0005480980 strike 70 (repo 2%, vol 8%: forward 69,886, call 0,816, valore 816 su 100.000), con parità put-call esatta. **Form React fatto**: *Pricing → Options* (`/option-p`, `OptionPView` + `OptionPForm` + `OptionDetails`); la lista unisce le serie delle quattro famiglie (`fetchOptionMasterDataDto`: `/master_data/r01/{FFO|BFO|FXO|BDO}`, famiglia aggiunta lato client) e i campi di mercato cambiano con la famiglia (prezzo del future / spot / prezzo secco; curva e tasso di sconto, curve domestica ed estera, curva e tasso repo); volatilità in %, premio di mercato facoltativo per la vol implicita. Restano: evaluator MtM delle opzioni; smile/volatilità implicite per testata, scadenza e strike in una tabella di dati di mercato; import dei settlement CME con calcolo e salvataggio della vol implicita.
2. **Fase B — MtM e dati di mercato**: verificare a runtime evaluator FX/CMD e MtM dei future con la nuova anagrafica; quotazioni per scadenza; tempo alla scadenza in frazioni di giorno usando orario e fuso della testata.
3. **Fase C — interfacce**: finestre di `master_data_mgr` (togliere la combo daycount dai future, scelta esplicita della testata), form React di `easy_pricer_cli` e JSON REST dei future.
4. **Test automatici** con i casi validati (Borsa Italiana, Mini BTP incl. yield/DV01 del CTD, CME). In `TestPricer` il test FX è commentato.
5. Punti aperti minori: scelta bid/mid/ask in `BondForwardCalculator.spotCleanPrice` (oggi BID), controllo data delle quotazioni, estrapolazione `YieldCurve`, rateo per daycount diversi, `Frequency.getYearFraction()`.
