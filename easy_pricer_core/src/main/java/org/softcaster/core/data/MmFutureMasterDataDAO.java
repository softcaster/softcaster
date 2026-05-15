package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("mmFutureMasterDataDAO")
public class MmFutureMasterDataDAO {

    @Resource
    private MmFutureMasterDataRepository repository;
    @Resource
    private InstrumentQuoteRepository quoteRepository;
    
    private final Sort sortByIsin = Sort.by(Sort.Direction.ASC, "isin");

    @Transactional(readOnly = true)
    public MmFutureMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public MmFutureMasterData saveOrUpdate(MmFutureMasterData mmFutureMasterData) {
        return repository.save(mmFutureMasterData);
    }

    @Transactional
    public void delete(MmFutureMasterData mmFutureMasterData) {
        if (mmFutureMasterData != null && mmFutureMasterData.getIdMasterData() != null) {
            // Va cancellata prima la tabella storica che ha un riferimento
            // alla tabella instrumet_quotes
            quoteRepository.deleteInstrumentQuoteHist(mmFutureMasterData.getIdMasterData());
            quoteRepository.deleteInstrumentQuotes(mmFutureMasterData.getIdMasterData());
            repository.delete(mmFutureMasterData);
        }
    }

    @Transactional(readOnly = true)
    public List<MmFutureMasterData> findAll() {
        return repository.findAll(sortByIsin);
    }

    public MmFutureMasterData findByIsin(String isin) {
        return repository.findByIsin(isin);
    }
}
