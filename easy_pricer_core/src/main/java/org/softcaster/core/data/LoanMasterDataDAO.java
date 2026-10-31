package org.softcaster.core.data;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Mutui (loan_master_data, flt_loan_master_data).
 */
@Repository("loanMasterDataDAO")
public class LoanMasterDataDAO extends AbstractMasterDataDAO<LoanMasterData, LoanMasterDataRepository> {

    public LoanMasterDataDAO(LoanMasterDataRepository repository) {
        super(repository);
    }

    /**
     * Nuovo mutuo con piano e spese. Il codice contiene l'id, che la sequence
     * assegna al salvataggio: si salva con un codice provvisorio e si aggiorna
     * nella stessa transazione.
     */
    @Transactional
    public LoanMasterData create(LoanMasterData lmd) {
        if (lmd.getIdMasterData() != null) {
            throw new IllegalArgumentException("Loan " + lmd.getCode() + " already saved");
        }
        lmd.setCode("NEW-" + UUID.randomUUID().toString().substring(0, 18));
        LoanMasterData saved = repository.saveAndFlush(lmd);
        saved.setCode(saved.contractCode());
        return saved;
    }

    @Transactional(readOnly = true)
    public LoanMasterData getByCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Missing loan code");
        }
        LoanMasterData lmd = repository.findByCodeWithCashFlows(code)
                .orElseThrow(() -> new NoSuchElementException("Loan not found: " + code));
        lmd.getFees().size();
        return lmd;
    }

    @Transactional(readOnly = true)
    public LoanMasterData getById(Integer id) {
        LoanMasterData lmd = repository.findByIdWithCashFlows(id)
                .orElseThrow(() -> new NoSuchElementException("Loan not found: " + id));
        lmd.getFees().size();
        return lmd;
    }

    /**
     * Tutti i mutui, piu' recenti per primi, con piano e spese.
     */
    @Transactional(readOnly = true)
    @Override
    public List<LoanMasterData> findAll() {
        List<LoanMasterData> loans = repository.findAllWithCashFlows();
        loans.forEach(l -> l.getFees().size());
        return loans;
    }
}
