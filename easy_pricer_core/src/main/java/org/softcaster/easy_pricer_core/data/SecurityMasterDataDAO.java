package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("securityMasterDataDAO")
public class SecurityMasterDataDAO {

    @Resource
    private SecurityMasterDataRepository repository;

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
        repository.delete(securityMasterData);
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findAll() {
        return repository.findAll(sortByMaturity);
    }

    public SecurityMasterData findByIsin(String isin) {
        return repository.findByIsin(isin);
    }

    public List<SecurityMasterData> findByCurrency(String currencyCode) {
        return repository.findByCurrencyIsoCode(currencyCode);
    }

    public List<SecurityMasterData> findByIssueDescriptionContaining(String issueDescriptionFragment) {
        return repository.findByIssueDescriptionContaining(issueDescriptionFragment);
    }
}
