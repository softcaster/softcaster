package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("bondFutureMasterDataDAO")
public class BondFutureMasterDataDAO {

    @Resource
    private BondFutureMasterDataRepository repository;
    @Resource
    private InstrumentQuoteRepository quoteRepository;

    private final Sort sortByIsin = Sort.by(Sort.Direction.ASC, "isin");

    @Transactional(readOnly = true)
    public BondFutureMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public BondFutureMasterData saveOrUpdate(BondFutureMasterData bondFutureMasterData) {
        return repository.save(bondFutureMasterData);
    }

    @Transactional
    public void delete(BondFutureMasterData bondFutureMasterData) {
        if (bondFutureMasterData != null && bondFutureMasterData.getIdMasterData() != null) {
            // Va cancellata prima la tabella storica che ha un riferimento
            // alla tabella instrumet_quotes
            quoteRepository.deleteInstrumentQuoteHist(bondFutureMasterData.getIdMasterData());
            quoteRepository.deleteInstrumentQuotes(bondFutureMasterData.getIdMasterData());
            repository.delete(bondFutureMasterData);
        }
    }

    @Transactional(readOnly = true)
    public List<BondFutureMasterData> findAll() {
        return repository.findAll(sortByIsin);
    }

    public BondFutureMasterData findByIsin(String isin) {
        return repository.findByIsin(isin);
    }

    public List<BondFutureMasterData> findAllByAssetClass(String code) {
        return repository.findAllByAssetClass(code);
    }
}
