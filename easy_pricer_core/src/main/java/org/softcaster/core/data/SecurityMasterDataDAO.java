package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("securityMasterDataDAO")
public class SecurityMasterDataDAO {

    @Resource
    private SecurityMasterDataRepository repository;
    @Resource
    private InstrumentQuoteRepository quoteRepository;

    private final Sort sortByMaturity = Sort.by(Sort.Direction.ASC, "maturityDate");

    @Transactional(readOnly = true)
    public SecurityMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public SecurityMasterData saveOrUpdate(SecurityMasterData securityMasterData) {
        return repository.save(securityMasterData);
    }

    @Transactional
    public void delete(SecurityMasterData securityMasterData) {
        if (securityMasterData != null && securityMasterData.getIdMasterData() != null) {
            quoteRepository.deleteInstrumentQuotes(securityMasterData.getIdMasterData());
            quoteRepository.deleteInstrumentQuoteHist(securityMasterData.getIdMasterData());
            repository.delete(securityMasterData);
        }
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findAll() {
        return repository.findAll(sortByMaturity);
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findAllByAssetClass(String code) {
        return repository.findAllByAssetClass(code);
    }

    @Transactional(readOnly = true)
    public SecurityMasterData findByIsin(String isin) {
        return repository.findByIsin(isin);
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findByCurrency(String currencyCode) {
        return repository.findByCurrencyIsoCode(currencyCode);
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findByDescriptionContaining(String issueDescriptionFragment) {
        return repository.findByDescriptionContaining(issueDescriptionFragment);
    }

    @Transactional
    public SecurityMasterData saveAndFlush(SecurityMasterData securityMasterData) {
        return repository.saveAndFlush(securityMasterData);
    }

    @Transactional
    public void deleteCashFlowItems(Integer idMasterData) {
        repository.deleteCashFlowsByMasterDataId(idMasterData);
    }
}
