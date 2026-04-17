package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("fxFutureMasterDataDAO")
public class FxFutureMasterDataDAO {

    @Resource
    private FxFutureMasterDataRepository repository;
    @Resource
    private InstrumentQuoteRepository quoteRepository;

    @Transactional(readOnly = true)
    public FxFutureMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public FxFutureMasterData saveOrUpdate(FxFutureMasterData fxFutureMasterData) {
        return repository.save(fxFutureMasterData);
    }

    @Transactional
    public void delete(FxFutureMasterData fxFutureMasterData) {
        if (fxFutureMasterData != null && fxFutureMasterData.getIdMasterData() != null) {
            // Va cancellata prima la tabella storica che ha un riferimento
            // alla tabella instrumet_quotes
            quoteRepository.deleteInstrumentQuoteHist(fxFutureMasterData.getIdMasterData());
            quoteRepository.deleteInstrumentQuotes(fxFutureMasterData.getIdMasterData());
            repository.delete(fxFutureMasterData);
        }
    }

    @Transactional(readOnly = true)
    public List<FxFutureMasterData> findAll() {
        return repository.findAll();
    }

    public FxFutureMasterData findByIsin(String isin) {
        return repository.findByIsin(isin);
    }
}
