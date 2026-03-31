package org.softcaster.easy_pricer_core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityMasterDataRepository extends JpaRepository<SecurityMasterData, Integer> {

    public SecurityMasterData findByIdMasterData(Integer idMasterData);

    public SecurityMasterData findByIsin(String isin);

    public List<SecurityMasterData> findByCurrencyIsoCode(String currencyCode);
    
    List<SecurityMasterData> findByIssueDescriptionContaining(String issueDescriptionFragment);}
