/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.services;

/**
 *
 * @author softc
 */
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Objects;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.dto.FinancialTxnDto;
import org.softcaster.core.dto.FinancialTxnMapper;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_srv.exceptions.FinancialTxnException;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.TxnStatus;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class FinancialTxnService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private TransactionTemplate transactionTemplate; // Gestore manuale della transazione
    @Autowired
    private FinancialTxnDAO dao;
    @Autowired
    private FinancialTxnMapper mapper;
    @Autowired
    private SystemBusinessCalendarDAO systemBusinessCalendarDAO;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    @PostConstruct
    public void init() {
        this.marketDataService.loadSpotPrice();
    }

    // Cancellazione logica marca solamente la transazione a TO_CANCEL ma
    // non la rimuove fisicamente da DB
    public FinancialTxnDto logicalDelete(Integer idFinancialTxn) {

        return transactionTemplate.execute(status -> {
            FinancialTxnDto txnDto = null;
            FinancialTxn databaseTxn = dao.findByIdFinancialTxn(idFinancialTxn);
            if (databaseTxn != null) {
                databaseTxn.setTxnStatus(TxnStatus.TO_CANCEL);
                databaseTxn.setTxnStatusPreElab(TxnStatus.TO_CANCEL);
                txnDto = mapper.toDto(databaseTxn);
                // Salvo
                dao.saveOrUpdate(databaseTxn);
                entityManager.flush();
            }

            return txnDto;
        });
    }

    public FinancialTxnDto saveOrUpdateTransaction(FinancialTxnDto newTxnDto) {
        // Avviamo una transazione esplicita tramite il template. Tutto il blocco è atomico (tutto o niente).
        return transactionTemplate.execute(status -> {

            // 1. Recuperiamo l'oggetto DTO originale prima che il mapper lo modifichi in memoria
            FinancialTxnDto oldTxnDto = null;
            // Devo salvare la version per il caso di modifica di una
            // transazione PENDING, vedi commento alla funzione fromDto
            Integer version = null;
            if (newTxnDto.financialTxnId() > 0) {
                FinancialTxn databaseTxn = dao.findByIdFinancialTxn(newTxnDto.financialTxnId());
                if (databaseTxn != null) {
                    oldTxnDto = mapper.toDto(databaseTxn);
                    version = databaseTxn.getVersion();
                }
            }

            // Mappiamo il DTO per estrarre i dati aggiornati dal client
            // Crea sempre una nuova transazione, vedi commenti nel mapper
            FinancialTxn financialTxn = mapper.fromDto(newTxnDto);
            if (financialTxn == null) {
                throw new IllegalArgumentException("Null Transaction");
            }
            TxnStatus txnStatus = TxnStatus.fromId(newTxnDto.txnStatusId());
            switch (txnStatus) {
                case PENDING -> {
                    // NB vedi commenti in fromDto per questo controllo. 
                    // Se oldTxnDto = null allora nuova transazione inserita per la prima volta
                    // Se != null allora modifica di una transazione pending e basta semplicemente
                    // assegnare il txnId e salvarla
                    if (oldTxnDto == null) {
                        financialTxn.setIdFinancialTxn(null);
                        financialTxn.setTxnStatus(TxnStatus.PENDING);
                        financialTxn.setTxnStatusPreElab(TxnStatus.PENDING);
                        java.sql.Date settlementDate = calcSettlementDate(financialTxn);
                        financialTxn.setSettlement(settlementDate);
                        financialTxn.setValueDate(settlementDate);
                        financialTxn.setTxnAcctPhase(AccountingPhase.NONE);
                        financialTxn.setFxRate(getFxRate(financialTxn));
                    } else {
                        financialTxn.setIdFinancialTxn(oldTxnDto.financialTxnId());
                        financialTxn.setTxnStatusPreElab(TxnStatus.PENDING);
                        financialTxn.setVersion(version);
                    }
                    // Usiamo il DAO coerentemente con il resto dello switch
                    dao.saveOrUpdate(financialTxn);
                }
                case TO_AMEND -> {
                    // Se la modifica tocca dati contabili o strutturali importanti
                    if (!updateOnly(oldTxnDto, newTxnDto)) {

                        // lo stato del vecchio record a TO_AMEND
                        FinancialTxn oldTxnToCancel = dao.findByIdFinancialTxn(oldTxnDto.financialTxnId());
                        oldTxnToCancel.setTxnStatus(TxnStatus.TO_AMEND);
                        oldTxnToCancel.setTxnStatusPreElab(TxnStatus.TO_AMEND);
                        oldTxnToCancel.setTxnAcctPhase(AccountingPhase.REVERSED);
                        dao.saveOrUpdate(oldTxnToCancel);

                        // Crea un record Java nuovo (Nasce slegato dalla sessione di Hibernate)
                        financialTxn.setIdFinancialTxn(null); // Forza la INSERT di una riga nuova
                        financialTxn.setRefId(oldTxnToCancel.getIdFinancialTxn()); // Copia l'ID originale (es. 34)
                        financialTxn.setTxnStatus(TxnStatus.PENDING);
                        financialTxn.setTxnStatusPreElab(TxnStatus.PENDING);
                        financialTxn.setTxnAcctPhase(AccountingPhase.NONE);
                        financialTxn.setFxRate(getFxRate(financialTxn));

                        dao.saveOrUpdate(financialTxn);
                    } else {
                        // Modifica superficiale (es. solo descrizione): aggiorna il record esistente senza stornare
                        FinancialTxn txnToUpdate = dao.findByIdFinancialTxn(oldTxnDto.financialTxnId());
                        txnToUpdate.setDescription(newTxnDto.description());
                        dao.saveOrUpdate(txnToUpdate);
                    }
                }
                default ->
                    throw new IllegalArgumentException("Stato transazione non supportato");
            }

            // Forziamo PostgreSQL a scrivere le modifiche e a validare i vincoli prima del commit definitivo
            entityManager.flush();

            return newTxnDto;
        });
    }

    private boolean updateOnly(FinancialTxnDto oldDto, FinancialTxnDto newDto) {
        if (oldDto == null) {
            return false;
        }
        String oldTradeDateStr = oldDto.tradeDate() != null ? oldDto.tradeDate().toString() : null;
        String newTradeDateStr = newDto.tradeDate() != null ? newDto.tradeDate().toString() : null;
        return Objects.equals(oldDto.price(), newDto.price())
                && Objects.equals(oldDto.quantity(), newDto.quantity())
                && Objects.equals(oldDto.counterpartyId(), newDto.counterpartyId())
                && Objects.equals(oldDto.positionMdId(), newDto.positionMdId())
                && Objects.equals(oldTradeDateStr, newTradeDateStr)
                && Objects.equals(oldDto.txnSide(), newDto.txnSide());
    }

    private Double getFxRate(FinancialTxn financialTxn) {
        try {
            SystemBusinessCalendar sbc = systemBusinessCalendarDAO.findBySbcId(1);
            // Per Fx Spot torno cambio contrattuale
            if (financialTxn.getMasterData().getAssetClass().getCode().equals("FSP")) {
                return financialTxn.getPrice();
            } else {
                if (financialTxn.getMasterData().getCurrency().getIdCurrency().equals(sbc.getCurrency().getIdCurrency())) {
                    return 1.;
                } else {
                    String pair = sbc.getCurrency().getIsoCode() + financialTxn.getMasterData().getCurrency().getIsoCode();
                    double rate = marketDataService.getSpotPrice(pair, RequestType.BID);
                    return rate;
                }
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new FinancialTxnException(ex.getLocalizedMessage());
        }
    }

    private Date calcSettlementDate(FinancialTxn financialTxn) {

        if (financialTxn.getMasterData() != null) {
            List<Currency> currencies = financialTxn.getMasterData().getCurrencyList();
            if (currencies != null && !currencies.isEmpty()) {
                Calendar calendar = new Calendar(currencies);
                return java.sql.Date.valueOf(calendar.getNextBusinessDate(financialTxn.getTradeDate(), financialTxn.getMasterData().getBusinessDays()));

            }
        }
        return financialTxn.getTradeDate();
    }
}
