package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("financialTxnDAO")
public class FinancialTxnDAO {

    @Autowired
    private TxnStatusDAO txnStatusDAO;

    @Resource
    private FinancialTxnRepository repository;

    @Transactional(readOnly = true)
    public FinancialTxn findByIdFinancialTxn(Integer idFinancialTxn) {
        return repository.findByIdFinancialTxn(idFinancialTxn);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findByTxnStatusCode(String code) {
        return repository.findByTxnStatusCode(code);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAllByDaycountCode(String code) {
        return repository.findAllByDaycountCode(code);
    }

    @Transactional(readOnly = true)
    public List<FinancialTxn> findAllByAssetClass(String code) {
        return repository.findAllByAssetClass(code);
    }

    protected boolean updateOnly(FinancialTxn oldTxn, FinancialTxn newTxn) {

        if (oldTxn != null && newTxn != null) {
            // Comparo prezzi
            if (Double.compare(newTxn.getPrice(), oldTxn.getPrice()) != 0) {
                return false;
            }
            // Comparo quantita
            if (Double.compare(newTxn.getQuantity(), oldTxn.getQuantity()) != 0) {
                return false;
            }
            // Comparo controparte
            if (!newTxn.getCounterparty().getCode().equals(oldTxn.getCounterparty().getCode())) {
                return false;
            }
        }

        return true;
    }

    @Transactional
    public FinancialTxn saveOrUpdate(FinancialTxn financialTxn) {
        // Controllo se sono in update
        if (financialTxn.getIdFinancialTxn() != null && financialTxn.getIdFinancialTxn() > 0) {
            // Recupero txn da db per allineare lo stato
            FinancialTxn oldTxn = findByIdFinancialTxn(financialTxn.getIdFinancialTxn());
            financialTxn.setTxnStatus(oldTxn.getTxnStatus());
            if (financialTxn.getTxnStatus().getCode().equals("EXECUTED")) {
                // controllo se modifica che comporta cancellazione e nuovo
                // inserimento
                if (!updateOnly(oldTxn, financialTxn)) {
                    // Marco la vecchia txn come cancellata
                    oldTxn.setTxnStatus(txnStatusDAO.findByCode("CANCELLED"));
                    repository.save(oldTxn);
                    // Creo una nuova txn
                    financialTxn.setTxnStatus(txnStatusDAO.findByCode("PENDING"));
                    financialTxn.setIdFinancialTxn(null);
                    financialTxn.setRefId(oldTxn.getIdFinancialTxn());
                }
            }

        }
        return repository.save(financialTxn);
    }

    @Transactional
    public void delete(FinancialTxn financialTxn) {
        repository.delete(financialTxn);
    }

    // Cancellazione logica marca solamente la transazione a CANCELLED ma
    // non la rimuove fisicamente da DB
    @Transactional
    public FinancialTxn logicalDelete(Integer idFinancialTxn) {

        // Carico txn
        FinancialTxn financialTxn = findByIdFinancialTxn(idFinancialTxn);
        // Setto a CANCELLED
        financialTxn.setTxnStatus(txnStatusDAO.findByCode("CANCELLED"));
        // Salvo
        return saveOrUpdate(financialTxn);
    }

}
