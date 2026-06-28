package org.softcaster.core.data;

import java.util.List;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("financialTxnDAO")
public class FinancialTxnDAO {

    private final FinancialTxnRepository repository;

    public FinancialTxnDAO(FinancialTxnRepository repository) {
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public FinancialTxn findByIdFinancialTxn(Integer idFinancialTxn) {
        return repository.findByIdFinancialTxn(idFinancialTxn);
    }

    @Transactional(readOnly = true)
    public FinancialTxn findByIdWithMasterData(Integer idFinancialTxn) {
        return repository.findByIdWithMasterData(idFinancialTxn);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAll() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<FinancialTxn> findAndClaimByTxnStatusCode(String code/*, int maxResults*/) {
        // 1. Legge e BLOCCA le righe saltando quelle già prenotate da altre istanze (SKIP LOCKED)
        TxnStatus status = TxnStatus.fromCode(code); 
        List<FinancialTxn> txnList = repository.getAndLockByStatusCode(status/*, PageRequest.of(0, maxResults)*/);

        // 2. Aggiorna lo stato immediatamente all'interno della stessa transazione atomica
        for (FinancialTxn txn : txnList) {
            txn.setTxnStatus(TxnStatus.CLAIMED);
            repository.save(txn); // Salva nel contesto di persistenza
        }

        // Al return, il metodo finisce, la transazione fa COMMIT, 
        // lo stato sul DB diventa "CLAIMED" e i record sono al sicuro dalle altre istanze.
        return txnList;
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAllByDaycountCode(String code) {
        DaycountBasis daycount = DaycountBasis.fromCode(code);
        return repository.findAllByDaycount(daycount);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAllByAssetClass(String code) {
        return repository.findAllByAssetClass(code);
    }

    @Transactional
    public FinancialTxn saveOrUpdate(FinancialTxn financialTxn) {
        return repository.save(financialTxn);
    }

    @Transactional
    public void delete(FinancialTxn financialTxn) {
        repository.delete(financialTxn);
    }

    // Cancellazione logica marca solamente la transazione a TO_CANCEL ma
    // non la rimuove fisicamente da DB
    @Transactional
    public FinancialTxn logicalDelete(Integer idFinancialTxn) {

        // Carico txn
        FinancialTxn financialTxn = findByIdFinancialTxn(idFinancialTxn);
        // Setto a TO_CANCEL
        financialTxn.setTxnStatus(TxnStatus.TO_CANCEL);
        financialTxn.setTxnStatusPreElab(TxnStatus.TO_CANCEL);
        // Salvo
        return saveOrUpdate(financialTxn);
    }
}
